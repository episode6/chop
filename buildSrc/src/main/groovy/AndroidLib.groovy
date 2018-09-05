import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLib implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.with {
      plugins.with {
        apply 'com.android.library'
        apply 'com.episode6.hackit.deployable.aar'
        apply 'com.episode6.hackit.gdmc'
      }

      sourceCompatibility = 1.7
      targetCompatibility = 1.7

      android {
        compileSdkVersion gdmcVersion('android.compilesdk') as Integer

        buildTypes {
          release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
          }
        }

        compileOptions {
          sourceCompatibility JavaVersion.VERSION_1_7
          targetCompatibility JavaVersion.VERSION_1_7
        }
      }
    }
  }
}
