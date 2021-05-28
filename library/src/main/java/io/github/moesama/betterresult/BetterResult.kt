package io.github.moesama.betterresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <I, O> Activity.requestResult(
    key: String,
    contract: ActivityResultContract<I, O>,
    input: I? = null
): O? = suspendCoroutine { continuation ->
    if (this !is ComponentActivity) {
        continuation.resumeWithException(ClassCastException("${this::class.java.name} can not be cast to ${ComponentActivity::class.java.name}"))
        return@suspendCoroutine
    }
    createBro(key, contract) {
        continuation.resume(it)
    }.launch(this, input)
}

suspend fun <I, O> Fragment.requestResult(
    key: String,
    contract: ActivityResultContract<I, O>,
    input: I? = null
): O? = activity?.requestResult(key, contract, input)

fun <I, O> createContract(
    block: ContractBuilder<I, O>.() -> Unit
): ActivityResultContract<I, O> =
    ContractBuilder.create(block).build()

private fun <I, O> ComponentActivity.createBro(
    key: String,
    contract: ActivityResultContract<I, O>,
    func: (O?) -> Unit
): BetterResultObserver<I, O> =
    BetterResultObserver(key, activityResultRegistry, contract, func)

class BetterResultObserver<I, O> internal constructor(
    private val key: String,
    private val registry: ActivityResultRegistry,
    private val contract: ActivityResultContract<I, O>,
    private val func: (O?) -> Unit
) : DefaultLifecycleObserver {
    private var launcher: ActivityResultLauncher<I>? = null

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        launcher = registry.register(key, contract) {
            func(it)
            launcher?.unregister()
            launcher = null
            owner.lifecycle.removeObserver(this)
        }
    }

    fun launch(owner: LifecycleOwner, input: I? = null) {
        owner.lifecycle.addObserver(this)
        launcher?.launch(input)
    }
}

class ContractBuilder<I, O> internal constructor() {
    private lateinit var intentConfig: Intent.() -> Unit
    private var syncFunc: (context: Context, input: I?) -> O? = { _, _ -> null }
    private lateinit var parseFuc: (resultCode: Int, intent: Intent?) -> O?

    fun intent(block: Intent.() -> Unit) {
        intentConfig = block
    }

    fun sync(block: (context: Context, input: I?) -> O?) {
        syncFunc = block
    }

    fun parse(block: (resultCode: Int, intent: Intent?) -> O?) {
        parseFuc = block
    }

    fun build(): ActivityResultContract<I, O> = object : ActivityResultContract<I, O>() {
        override fun createIntent(context: Context, input: I): Intent = Intent().apply(intentConfig)

        override fun getSynchronousResult(context: Context, input: I): SynchronousResult<O>? =
            syncFunc.invoke(context, input)?.let { SynchronousResult(it) }

        override fun parseResult(resultCode: Int, intent: Intent?): O? =
            parseFuc.invoke(resultCode, intent)
    }

    companion object {
        fun <I, O> create(block: ContractBuilder<I, O>.() -> Unit): ContractBuilder<I, O> =
            ContractBuilder<I, O>().apply(block)
    }
}