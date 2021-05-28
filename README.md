# BetterResult

![min-sdk](https://img.shields.io/badge/minSdk-19-green)![target-sdk](https://img.shields.io/badge/targetSdk-30-blue) ![LICENSE](https://img.shields.io/github/license/moesama/BetterResult)

Easy Android New Activity Result Api Helper with kotlin coroutine



## How to

### Android Gradle

**Step 1.** Add the **JitPack** repository to your build file

Add it in your root `build.gradle` at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency

```groovy
dependencies {
    implementation 'com.github.moesama:BetterResult:1.0.0'
}
```



## Document

### RequestResult

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            GlobalScope.launch {
                val res = requestResult("pickImage", ActivityResultContracts.OpenDocument(), arrayOf("image/*"))
            }
            ...
        }
    }
}
```



### CreateContract

```kotlin
val contract = createContract<String, String> {
    initent {
        action = YOUR_ACTIVITY_ACTION
    }
    sync { context, input ->
        return "return Something not Null if startActivity is not needed and will return this value as result"
    }
    parse { resultCode, intent ->
        // parse the value to return from resultCode and intent
        if (resultCode == Activity.RESULT_OK) {
            intent?.data?.toString() ?: ""
        } else {
            ""
        }
    }
}
```

Here's an example for who's parse method need **Context**：

```kotlin
class App : Application() {
    @SuppressLint("InlinedApi")
    override fun onCreate() {
        super.onCreate()
        appUsageContracts = createContract {
            intent { action = Settings.ACTION_USAGE_ACCESS_SETTINGS }
            sync { context, _ -> if (context.checkAppUsagePermission()) true else null }
            parse { _, _ -> checkAppUsagePermission() }
        }
    }

    companion object {
        lateinit var appUsageContracts: ActivityResultContract<Nothing, Boolean>
            private set
    }
}
```



