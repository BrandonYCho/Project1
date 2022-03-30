package ui
import scala.io.StdIn.{readInt, readLine}
import db.HiveMaster

object MainMenu {
  private var state : String = ""

  def intro(): Unit = {
    val menuOptions: List[Int] = List(1, 2, 3)
    var menuChoice: Int = 0
    state = "Intro"

    while (state.equals("Intro")) {
      println()
      UserMenu.login()
      do {
        print("Please select an option: ")
        try {
          menuChoice = readInt()
          if (!menuOptions.contains(menuChoice))
            println("INVALID ENTRY\nPlease enter 1,2, or 3")
        } catch {
          case nfe: NumberFormatException =>
            println("INVALID ENTRY\nPlease enter 1,2, or 3")
        }
      } while (!menuOptions.contains(menuChoice))
      menuChoice match {
        case 1 => login()
        case 2 => registerAccount()
        case 3 => state = "Exit"
      }

      menuChoice = 0
    }
  }

  private def login(): Unit = {
    var username: String = ""
    var password: String = ""
    state = "Login"

    while (state.equals("Login")) {
      println()
      print("(enter nothing to cancel)\nUsername: ")
      username = readLine()
      if (username.isEmpty)
        state = "Intro"
      else {
        print("(enter nothing to cancel)\nPassword: ")
        password = readLine()
        if (password.isEmpty)
          state = "Intro"
        else if (HiveMaster.authenticate(username, password, isAdmin = false) > 0) {
          BasicMaster.authorize(username, password)
          state = "Intro"
        }
        else if (HiveMaster.authenticate(username, password, isAdmin = true) > 0) {
          AdminMaster.authorize(username, password)
          state = "Intro"
        }
        else
          println("Credentials do not match\nPlease try again")
      }
    }
  }

  private def registerAccount(): Unit = {
    var username: String = ""
    var password: String = ""
    state = "Register"

    while (state.equals("Register")) {
      println()
      do {
        print("(enter nothing to cancel)\nUsername: ")
        username = readLine()
        if (HiveMaster.usernameExists(username))
          println("Username is already taken")
      } while (HiveMaster.usernameExists(username))

      if (username.isEmpty)
        state = "Intro"
      else {
        do {
          print("Password: ")
          password = readLine()
          if (password.isEmpty)
            println("Password can't be empty\nPlease try again")
        } while (password.isEmpty)

        HiveMaster.addUser(username, password)
        println(s"User $username created")
        state = "Intro"
      }
    }
  }
}
