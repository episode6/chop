import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinLib implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.with {
      plugins.with {
        apply 'kotlin'
        apply 'com.episode6.hackit.deployable.kt.jar'
        apply 'com.episode6.hackit.gdmc'
      }

      sourceCompatibility = 1.7
      targetCompatibility = 1.7

      dependencies {
        implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk7'
      }
    }
  }
}
