package ui

import db.HiveMaster
import scala.io.StdIn.{readLine, readInt}

object BasicMaster {
  private var userID: Int = 0
  private var state: String = ""

  def authorize(username: String, password: String): Unit = {
    userID = HiveMaster.authenticate(username, password, false)
    if (userID > 0) {
      println("Login successful")
      homeScreen()
    }
  }

  private def homeScreen(): Unit = {
    state = "Home Screen"
    while (state.equals("Home Screen")) {
      println()

      userMenu()
    }
  }

  private def userMenu(): Unit = {
    state = "User Menu"
    var userInput: Int = 0
    val menuOptions: List[Int] = List(1, 2, 3, 4, 5)

    while (state.equals("User Menu")) {
      println()
      BasicView.userMenu()
      do {
        print("Please select an option: ")
        try {
          userInput = readInt()
          if (!menuOptions.contains(userInput))
            println("INVALID ENTRY\nPlease enter 1, 2, 3, or 4")
        } catch {
          case nfe : NumberFormatException => {
            println("INVALID ENTRY\nPlease enter 1, 2 ,3, or 4")
          }
        }
      } while (!menuOptions.contains(userInput))

      userInput match {
        case 5 => state = "Home Screen";
        case 1 => executeQuery();
        case 2 => changeUsername();
        case 3 => changePassword();
        case 4 => state = "Intro";
      }

      userInput = 0
    }
  }

  private def executeQuery(): Unit = {
    state = "Execute Query"

    while (state.equals("Execute Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()
      if (queries.isEmpty) {
        println("No saved queries found")
        UserMenu.returning()
        state = "User Menu"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query. ${queries(query)}")
        do {
          print("(enter 0 to return)\nPlease select a query: ")
          try {
            userInput = readInt()
            if (!queries.keySet.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again.")
          } catch {
            case nfe : NumberFormatException => {
              println("INVALID ENTRY\nPlease try again.")
            }
          }
        } while (!queries.keySet.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "User Menu"
        else if (!HiveMaster.queryExists(userInput)) {
          println("Uh-oh!\nPlease try again")
          Thread.sleep(2000)
        } else {
          HiveMaster.showQuery(userInput)
          println()
          println("Would you like to save your results?")
          print("(enter nothing to cancel)\nEnter 'y' to continue: ")
          val choice: String = readLine()
          if (choice.toLowerCase().equals("y")) {
            print("(enter nothing to cancel)\nPlease enter the local filename/full file path: ")
            val filePath: String = readLine()
            if (filePath.nonEmpty)
              HiveMaster.exportQueryResults(userInput, filePath)
          }
        }

        userInput = -1
      }
    }
  }

  private def changeUsername(): Unit = {
    state = "Change Username"
    while (state.equals("Change Username")) {
      var userInput: String = ""
      do {
        println()
        print("(enter nothing to cancel)\nPlease enter your new username: ")
        userInput = readLine()
        if (HiveMaster.usernameExists(userInput))
          println(s"$userInput is already taken \nPlease choose another username")
      } while (HiveMaster.usernameExists(userInput))

      if (userInput.nonEmpty)
        if (!HiveMaster.usernameExists(userInput)) {
          HiveMaster.updateUsername(userID, userInput)
          println(s"Username successfully changed to $userInput")
          UserMenu.returning()
          state = "User Menu"
        } else {
          println("Uh-oh! There was a problem\nPlease try again")
          UserMenu.returning()
        }
      else
        state = "User Menu"
    }
  }

  private def changePassword(): Unit = {
    state = "Change Password"
    while (state.equals("Change Password")) {
      var oldPassword: String = ""
      var newPassword: String = ""

      print("(enter nothing to cancel)\nFirst enter your old password: ")
      oldPassword = readLine()
      if (oldPassword.nonEmpty) {
        print("(enter nothing to cancel)\nPlease enter your new password: ")
        newPassword = readLine()
        if (newPassword.nonEmpty)
          if (HiveMaster.updatePassword(userID, oldPassword, newPassword)) {
            print("Password successfully changed")
            UserMenu.returning()
            state = "User Menu"
          } else {
            println("Old password does not match. Please try again.")
            Thread.sleep(2000)
          }
        else
          state = "User Menu"
      } else
        state = "User Menu"
    }
  }
}
