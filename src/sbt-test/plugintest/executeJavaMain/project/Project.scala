import com.schantz.sbt._
import com.schantz.sbt.PluginKeys._
import sbt._
import Keys._

object MyBuild extends SchantzBuild {
	System.setProperty("dbBuildName", "hat")
  override def mySettings = {
    super.mySettings ++ DBBuildPlugin.dbBuildSettings ++ Seq(
      dbBuildClass := "com.schantz.foundation.util.db.mssql.DipDbBuilderTest",
      dbBuildPath := "//nas.schantz.com/GalopDbUpload/dbDumps/unpatched/Dip/",
      dbBuildName := ""
    )
  }
}
