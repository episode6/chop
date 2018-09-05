import org.gradle.api.Plugin
import org.gradle.api.Project

class GroovyLib implements Plugin<Project> {

  @Override
  void apply(Project project) {
    project.with {
      plugins.with {
        apply 'groovy'
        apply 'com.episode6.hackit.deployable.jar'
        apply 'com.episode6.hackit.deployable.addon.groovydocs'
        apply 'com.episode6.hackit.gdmc'
      }

      dependencies {
        compile localGroovy()
      }
    }
  }
}
