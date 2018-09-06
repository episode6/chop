import org.gradle.api.Plugin
import org.gradle.api.Project

class JavaLib implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.with {
      plugins.with {
        apply 'java-library'
        apply 'com.episode6.hackit.deployable.jar'
        apply 'com.episode6.hackit.gdmc'
      }
      sourceCompatibility = 1.7
      targetCompatibility = 1.7
    }
  }
}
