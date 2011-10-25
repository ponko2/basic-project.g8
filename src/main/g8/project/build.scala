import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "$organization$"
  val buildVersion      = "0.1.0"
  val buildScalaVersion = "$scala_version$"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    shellPrompt  := ShellPrompt.buildShellPrompt
  )
}

object ShellPrompt {
  object devnull extends ProcessLogger {
    def info  (s: => String) {}
    def error (s: => String) {}
    def buffer[T] (f: => T): T = f
  }

  def currBranch = (
    ("git status -sb" lines_! devnull headOption)
      getOrElse "-" stripPrefix "## "
  )

  val buildShellPrompt = {
    (state: State) => {
      val currProject = Project.extract (state).currentProject.id
      "%s:%s:%s> ".format (
        currProject, currBranch, BuildSettings.buildVersion
      )
    }
  }
}

object Dependencies {
  val scalatest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
}

object AndroidBuild extends Build {
  import Dependencies._
  import BuildSettings._

  val mainDeps = Seq(scalatest)

  lazy val main = Project (
    "$name$",
    file("."),
    settings = buildSettings ++ Seq(libraryDependencies ++= mainDeps)
  )
}
