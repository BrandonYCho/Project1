package ui

import db.{HiveMaster, SparkConnection}
import org.apache.spark.sql.{AnalysisException, DataFrame}

import scala.io.StdIn.{readInt, readLine}

object AdminMaster {
  private var userID: Int = 0
  private var state: String = ""

  def authorize(username: String, password: String): Unit = {
    userID = HiveMaster.authenticate(username, password, true)
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
      AdminView.userMenu()
      do {
        print("Please select an option: ")
        try {
          userInput = readInt()
          if (!menuOptions.contains(userInput))
            println("INVALID ENTRY\nPlease enter 1, 2, 3, 4, or 5")
        } catch {
          case nfe: NumberFormatException =>
            println("INVALID ENTRY\nPlease enter 1, 2, 3, 4, or 5")
        }
      } while (!menuOptions.contains(userInput))

      userInput match {
        case 1 => manageQueries();
        case 2 => manageUsers();
        case 3 => changeUsername();
        case 4 => changePassword();
        case 5 => state = "Intro";
      }
      userInput = 0
    }
  }

  private def manageQueries(): Unit = {
    state = "Manage Queries"
    var userInput: Int = -1
    val menuOptions: List[Int] = List(1, 2, 3, 4, 5, 6, 7)

    while (state.equals("Manage Queries")) {
      println()
      AdminView.manageQueries()
      do {
        print("Please select an option: ")
        try {
          userInput = readInt()
          if (!menuOptions.contains(userInput) && userInput != 0)
            println("INVALID ENTRY\nPlease enter 1, 2, 3, 4, 5, 6, or 7")
        } catch {
          case nfe: NumberFormatException =>
            println("INVALID ENTRY\nPlease enter 1, 2, 3, 4, 5, 6, or 7")
        }
      } while (!menuOptions.contains(userInput) && userInput != 0)

      userInput match {
        case 1 => HiveMaster.executeQuery1();
        case 2 => HiveMaster.executeQuery2();
        case 3 => HiveMaster.executeQuery3();
        case 4 => HiveMaster.executeQuery4();
        case 5 => HiveMaster.executeQuery5();
        case 6 => HiveMaster.executeQuery6();
        case 7 => state = "User Menu";
      }

      userInput = -1
    }
  }
/*
  private def manageQueries(): Unit = {
    state = "Manage Queries"
    var userInput: Int = -1
    val menuOptions: List[Int] = List(1, 2, 3, 4, 5)

    while (state.equals("Manage Queries")) {
      println()
      AdminView.manageQueries()
      do {
        print("Please select an option: ")
        try {
          userInput = readInt()
          if (!menuOptions.contains(userInput) && userInput != 0)
            println("INVALID ENTRY\nPlease enter 1, 2, 3, 4, or 5")
        } catch {
          case nfe: NumberFormatException =>
            println("INVALID ENTRY\nPlease enter 1, 2, 3, 4, or 5")
        }
      } while (!menuOptions.contains(userInput) && userInput != 0)

      userInput match {
        case 1 => executeQuery();
        case 2 => renameQuery();
        case 3 => writeQuery();
        case 4 => deleteQuery();
        case 5 => state = "User Menu";
      }

      userInput = -1
    }
  }
 */



  private def executeQuery2(): Unit = {
    state = "Execute Query"

    while (state.equals("Execute Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()


      if (queries.isEmpty) {
        println("No queries found!")
        UserMenu.returning()
        state = "Manage Queries"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query.${queries(query)}")
        do {
          print("(enter 0 to return)\nPlease select a query: ")
          try {
            userInput = readInt()
            if (!queries.keySet.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again")
          }
        } while (!queries.keySet.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "Manage Queries"
        else if (!HiveMaster.queryExists(userInput)) {
          println("Uh-oh! There was a problem\nPlease try again")
          UserMenu.returning()
        } else {
          HiveMaster.showQuery(userInput)
          Thread.sleep(2000)
          val choice: String = readLine("Would you like to save your results?\n(enter nothing to cancel)\nEnter 'y' to save: ")
          if (choice.toLowerCase().equals("y")) {
            val filePath: String = readLine("(enter nothing to cancel)\nPlease enter the local filename/full file path: ")
            if (filePath.nonEmpty)
              HiveMaster.exportQueryResults(userInput, filePath)
          }
        }

        userInput = -1
      }
    }
  }

  private def executeQuery3(): Unit = {
    state = "Execute Query"

    while (state.equals("Execute Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()


      if (queries.isEmpty) {
        println("No queries found!")
        UserMenu.returning()
        state = "Manage Queries"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query.${queries(query)}")
        do {
          print("(enter 0 to return)\nPlease select a query: ")
          try {
            userInput = readInt()
            if (!queries.keySet.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again")
          }
        } while (!queries.keySet.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "Manage Queries"
        else if (!HiveMaster.queryExists(userInput)) {
          println("Uh-oh! There was a problem\nPlease try again")
          UserMenu.returning()
        } else {
          HiveMaster.showQuery(userInput)
          Thread.sleep(2000)
          val choice: String = readLine("Would you like to save your results?\n(enter nothing to cancel)\nEnter 'y' to save: ")
          if (choice.toLowerCase().equals("y")) {
            val filePath: String = readLine("(enter nothing to cancel)\nPlease enter the local filename/full file path: ")
            if (filePath.nonEmpty)
              HiveMaster.exportQueryResults(userInput, filePath)
          }
        }

        userInput = -1
      }
    }
  }

  private def executeQuery4(): Unit = {
    state = "Execute Query"

    while (state.equals("Execute Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()


      if (queries.isEmpty) {
        println("No queries found!")
        UserMenu.returning()
        state = "Manage Queries"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query.${queries(query)}")
        do {
          print("(enter 0 to return)\nPlease select a query: ")
          try {
            userInput = readInt()
            if (!queries.keySet.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again")
          }
        } while (!queries.keySet.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "Manage Queries"
        else if (!HiveMaster.queryExists(userInput)) {
          println("Uh-oh! There was a problem\nPlease try again")
          UserMenu.returning()
        } else {
          HiveMaster.showQuery(userInput)
          Thread.sleep(2000)
          val choice: String = readLine("Would you like to save your results?\n(enter nothing to cancel)\nEnter 'y' to save: ")
          if (choice.toLowerCase().equals("y")) {
            val filePath: String = readLine("(enter nothing to cancel)\nPlease enter the local filename/full file path: ")
            if (filePath.nonEmpty)
              HiveMaster.exportQueryResults(userInput, filePath)
          }
        }

        userInput = -1
      }
    }
  }

  private def executeQuery5(): Unit = {
    state = "Execute Query"

    while (state.equals("Execute Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()


      if (queries.isEmpty) {
        println("No queries found!")
        UserMenu.returning()
        state = "Manage Queries"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query.${queries(query)}")
        do {
          print("(enter 0 to return)\nPlease select a query: ")
          try {
            userInput = readInt()
            if (!queries.keySet.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again")
          }
        } while (!queries.keySet.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "Manage Queries"
        else if (!HiveMaster.queryExists(userInput)) {
          println("Uh-oh! There was a problem\nPlease try again")
          UserMenu.returning()
        } else {
          HiveMaster.showQuery(userInput)
          Thread.sleep(2000)
          val choice: String = readLine("Would you like to save your results?\n(enter nothing to cancel)\nEnter 'y' to save: ")
          if (choice.toLowerCase().equals("y")) {
            val filePath: String = readLine("(enter nothing to cancel)\nPlease enter the local filename/full file path: ")
            if (filePath.nonEmpty)
              HiveMaster.exportQueryResults(userInput, filePath)
          }
        }

        userInput = -1
      }
    }
  }

  private def executeQuery6(): Unit = {
    state = "Execute Query"

    while (state.equals("Execute Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()


      if (queries.isEmpty) {
        println("No queries found!")
        UserMenu.returning()
        state = "Manage Queries"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query.${queries(query)}")
        do {
          print("(enter 0 to return)\nPlease select a query: ")
          try {
            userInput = readInt()
            if (!queries.keySet.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again")
          }
        } while (!queries.keySet.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "Manage Queries"
        else if (!HiveMaster.queryExists(userInput)) {
          println("Uh-oh! There was a problem\nPlease try again")
          UserMenu.returning()
        } else {
          HiveMaster.showQuery(userInput)
          Thread.sleep(2000)
          val choice: String = readLine("Would you like to save your results?\n(enter nothing to cancel)\nEnter 'y' to save: ")
          if (choice.toLowerCase().equals("y")) {
            val filePath: String = readLine("(enter nothing to cancel)\nPlease enter the local filename/full file path: ")
            if (filePath.nonEmpty)
              HiveMaster.exportQueryResults(userInput, filePath)
          }
        }

        userInput = -1
      }
    }
  }

  private def executeQuery(): Unit = {
    state = "Execute Query"

    while (state.equals("Execute Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()


      if (queries.isEmpty) {
        println("No queries found!")
        UserMenu.returning()
        state = "Manage Queries"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query.${queries(query)}")
        do {
          print("(enter 0 to return)\nPlease select a query: ")
          try {
            userInput = readInt()
            if (!queries.keySet.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again")
          }
        } while (!queries.keySet.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "Manage Queries"
        else if (!HiveMaster.queryExists(userInput)) {
          println("Uh-oh! There was a problem\nPlease try again")
          UserMenu.returning()
        } else {
          HiveMaster.showQuery(userInput)
          Thread.sleep(2000)
          val choice: String = readLine("Would you like to save your results?\n(enter nothing to cancel)\nEnter 'y' to save: ")
          if (choice.toLowerCase().equals("y")) {
            val filePath: String = readLine("(enter nothing to cancel)\nPlease enter the local filename/full file path: ")
            if (filePath.nonEmpty)
              HiveMaster.exportQueryResults(userInput, filePath)
          }
        }

        userInput = -1
      }
    }
  }

  private def renameQuery(): Unit = {
    state = "Rename Query"

    while (state.equals("Rename Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()
      if (queries.isEmpty) {
        println("No queries found")
        UserMenu.returning()
        state = "Manage Queries"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query. ${queries(query)}")
        do {
          print("(enter 0 to return)\nPlease select a query: ")
          try {
            userInput = readInt()
            if (!queries.keys.toList.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again")
          }
        } while (!queries.keys.toList.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "Manage Queries"
        else {
          var newName: String = ""
          do {
            print("(enter nothing to return)\nPlease enter a new name: ")
            newName = readLine()
            if (HiveMaster.queryNameExists(newName))
              println(s"$newName already exists\nPlease enter another name")
          } while (HiveMaster.queryNameExists(newName))

          if (newName.nonEmpty) {
            HiveMaster.renameQuery(userInput, newName)
            if (HiveMaster.getQueries().values.exists(q => q.equals(newName)))
              println(s"Query successfully renamed to $newName")
            else
              println("Uh-oh! There was a problem\nPlease try again")
            UserMenu.returning()
          }
        }
      }
    }
  }

  private def writeQuery(): Unit = {
    state = "Write Query"
    while (state.equals("Write Query")) {
      var query: String = ""
      println("(enter nothing to return)")
      println("Copy-and-paste (recommended) or type in your query and, when done, end it with a semicolon; for single-quotes, be sure to escape them with two backslashes (\\\\).);")
      do {
        query += readLine()
      } while (!query.contains(";"))
      if (query.replaceAll(";", "").nonEmpty) {
        try {
          HiveMaster.showQuery(query.replaceAll(";", ""))
        } catch {
          case ae: AnalysisException =>
            ae.printStackTrace()
        }
        println()
        println("Would you like to save your results?")
        print("(enter nothing to cancel)\nEnter 'y' to continue: ")
        val saveChoice: String = readLine()
        if (saveChoice.equals("y"))
          saveQuery(query)
      } else
        state = "Manage Queries"
    }
  }

  private def saveQuery(query: String): Unit = {
    var name: String = ""
    do {
      println("(enter nothing to return)\nEnter query name: ")
      name = readLine()
      if (HiveMaster.queryNameExists(name))
        println(s"Query $name already exists, please enter in another name.")
    } while (HiveMaster.queryNameExists(name))

    if (!HiveMaster.queryNameExists(name) && name.nonEmpty) {
      HiveMaster.saveQuery(userID, name, query)
      if (HiveMaster.queryNameExists(name))
        println(s"Query ${"\""}$name${"\""} saved successfully!")
    }
  }

  private def deleteQuery(): Unit = {
    state = "Delete Query"
    while (state.equals("Delete Query")) {
      println()
      val queries: Map[Int, String] = HiveMaster.getQueries()
      if (queries.isEmpty) {
        println("No saved queries found")
        UserMenu.returning()
        state = "Manage Queries"
      } else {
        var userInput: Int = -1
        for (query: Int <- queries.keys.toList.sorted)
          println(s"$query. ${queries(query)}")
        do {
          println("(enter 0 to return)")
          print("Please select a query to delete: ")
          try {
            userInput = readInt()
            if (!queries.keys.toList.contains(userInput) && userInput != 0)
              println("INVALID ENTRY\nPlease try again.")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again.")
          }
        } while (!queries.keys.toList.contains(userInput) && userInput != 0)

        if (userInput == 0)
          state = "Manage Queries"
        else {
          HiveMaster.deleteQuery(userInput)
          if (!HiveMaster.getQueries().keys.toList.contains(userInput))
            println(s"Query ${"\""}${queries(userInput)}${"\""} deleted successfully!")
          else
            println("Uh-oh! There was a problem\nPlease try again.")
          Thread.sleep(3000)
        }
      }
    }
  }

  private def manageUsers(): Unit = {
    state = "Manage Users"
    var userInput: Int = 0
    val menuOptions: List[Int] = List(1, 2)

    if (HiveMaster.getUsers().forall(p => p._4)) {
      println("There are no basic users")
      UserMenu.returning()
      state = "User Menu"
    }
    while (state.equals("Manage Users")) {
      val users: List[(Int, String, String, Boolean)] = HiveMaster.getUsers()
      /* Displays the users, don't really need it here since we have it below
      for(user: (Int, String, String, Boolean) <- users)
        if (!user._4) {
          println()
          println(s"ID: ${user._1} | Username: ${user._2} | Password: ${user._3}")
        }*/
      AdminView.manageUsers()
      do {
        print("Please select an option: ")
        try {
          userInput = readInt()
          if (!menuOptions.contains(userInput) && userInput != 0)
            println("INVALID ENTRY\nPlease try again.")
        } catch {
          case nfe : NumberFormatException =>
            println("INVALID ENTRY\nPlease try again.")
        }
      } while (!menuOptions.contains(userInput) && userInput != 0)
      if (userInput > 0) {
        var userChoice: Int = -1
        do {
          userInput match {
            case 1 =>
              for(user: (Int, String, String, Boolean) <- users)
                if (!user._4) {
                  println()
                  println(s"ID: ${user._1} | Username: ${user._2} | Password: ${user._3}")
                }
              print("(enter 0 to return)\nEnter an ID to delete that user: ");
            case 2 =>
              for(user: (Int, String, String, Boolean) <- users)
                if (!user._4) {
                  println()
                  println(s"ID: ${user._1} | Username: ${user._2} | Password: ${user._3}")
                }
              print("(enter 0 to return)\nSelect an ID to give that user Admin Privileges: ");
          }
          try {
            userChoice = readInt()
            if (!users.exists(u => u._1 == userChoice) && userChoice != 0)
              println("INVALID ENTRY\nPlease try again")
          } catch {
            case nfe: NumberFormatException =>
              println("INVALID ENTRY\nPlease try again")
          }
        } while (!users.exists(u => u._1 == userChoice) && userChoice != 0)

        if (userChoice > 0)
          userInput match {
            case 1 =>
              deleteBasicUser(userChoice)
              if (HiveMaster.getUsers().forall(p => p._4)) {
                println("There are no basic users")
                UserMenu.returning()
                state = "User Menu"
              };
            case 2 => elevateBasicUser(userChoice);
            case 0 => state = "User Menu";
          }

        userInput = -1
      } else
        state = "User Menu"
    }
  }

  private def elevateBasicUser(userID: Int): Unit = {
    HiveMaster.setToAdmin(userID)
    if (HiveMaster.isAdmin(userID))
      println("User given Admin privileges")
    else
      println("User already has Admin privileges")
    UserMenu.returning()
  }

  private def deleteBasicUser(userID: Int): Unit = {
    HiveMaster.deleteUser(userID)
    if (!HiveMaster.getUsers().exists(u => u._1 == userID))
      println("User deleted successfully")
    else
      println("User does not exist\nPlease select another user")
    UserMenu.returning()
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
            println("Old password does not match\nPlease try again")
            UserMenu.returning()
          }
        else
          state = "User Menu"
      } else
        state = "User Menu"
    }
  }
}

