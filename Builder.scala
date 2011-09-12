import sbt._
import Keys._
import java.io.File
import scala.xml._
object EclipseBuilderPlugin extends Plugin
{
    // configuration points, like the built in `version`, `libraryDependencies`, or `compile`
    // by implementing Plugin, these are automatically imported in a user's `build.sbt`
    //val newTask = TaskKey[Unit]("new-task")
    //val newSetting = SettingKey[String]("new-setting")

    // a group of settings ready to be added to a Project
    // to automatically add them, do 
    val newSettings = Seq(
        unmanagedJars in Compile ++= scanClassPath 
    )

    def scanClassPath = {
        val xml = XML.loadFile(".classpath")
        val jars = (xml \\ "classpathentry").filter(e => (e \\ "@kind").text == "lib").map(e => Attributed.blank(new File(".."+(e \\ "@path").text )))
        println("jars = "+jars)
        jars
    }
    // alternatively, by overriding `settings`, they could be automatically added to a Project
    // override val settings = Seq(...)
}