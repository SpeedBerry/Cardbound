<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">

![Cardbound Logo](https://github.com/SpeedBerry/Cardbound/blob/main/images/Logo.png?raw=true)

  <p align="center">
    An educational game that abstracts learning by using fun!
    <br />
    <a href="https://repo.csd.uwo.ca/projects/COMPSCI2212_W2024/repos/group40/browse"><strong>Bitbucket</strong></a>
    <br />
    <br />
  </p>
</div>


<details>
	<summary>Gameplay Images</summary>
	<img src="https://github.com/SpeedBerry/Cardbound/blob/main/images/MainMenu.png?raw=true" alt="Main Menu Image">
	The main menu in Cardbound. <br />
	![Gameplay Image](https://github.com/SpeedBerry/Cardbound/blob/main/images/Gameplay.png?raw=true) <br />
	What the gameplay of Cardbound looks like. <br />
	![Game Over Image](https://github.com/SpeedBerry/Cardbound/blob/main/images/GameOver.png?raw=true) <br />
	How the game over screen in Cardbound looks. <br />
</details>


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#running-cardbound">Running CARDBOUND</a></li>
      <ul>
        <li><a href="#user-and-game-save">User and Game Save</a></li>
      </ul>
    <li><a href="#user-guide">User Guide</a></li>
    <li><a href="#instructor-mode">Instructor Mode</a></li>
    <li><a href="#debug-mode">Debug Mode</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

**A short description of your software and what it does.**

This project aims to collide the worlds of math and gaming to create an explosion of joy and laughter while practicing crucial math fundamentals in a problem-solving environment. Catering to younger learners, we aim to help make the practice, a crucial part of learning math, not so boring and/or daunting. The problem-solving aspect helps the learner gain confidence in their own ability, along with learning to think independently and in ways that suit them. Through interactive gameplay and easily accessible interfaces, we foster an environment that ensures an engaging and fun learning experience, with a nice twist of competitiveness on the leaderboards with fellow classmates.

<p align="right">(<a href="#cardbound">back to top</a>)</p>



### Built With

**Major framework used to develop the project:**

* Javafx (Version 21-ea+5)
  * [![Javafx][Javafx.com]][Javafx-url]

**List of other libraries/dependencies:**

* Module com.google.gson (Version 2.10.1)
  * [API Docs here](https://javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/module-summary.html)
* JUnit (Version 5.9.2)
  * [API Docs here](https://junit.org/junit5/docs/5.10.2/release-notes/#release-notes-5.9.2)


<p align="right">(<a href="#cardbound">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Follow these steps to build CARDBOUND from the source code.

### Prerequisites

* Install Java Runtime (JRE 21.0.2)
  1. [Install Java Runtime 21 here](https://adoptium.net/temurin/archive/?version=21)
  2. Scroll down and navigate to the Windows x64, and click on the JRE installer
  3. Click next for every screen in the installation wizard (default download is acceptable)
* Verify java runtime version by running the following command in your terminal:
```shell
java --version
```
* Build the project with the Gradle build task
* Navigate to build/distributions/CARDBOUND-1.0.zip and extract the zip folder
* Ensure that your Windows Screen Scale value is set to 100%. The game will not work as intended on values above or below 100%.
* Run CARDBOUND.bat

### Installation

1. Download CARDBOUND-1.0.zip, extract
2. Run CARDBOUND.bat
3. Enjoy!

<p align="right">(<a href="#cardbound">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Running CARDBOUND

Once you've opened CARDBOUND, you will be greeted with the title screen. Here you can log in, or create a new profile if you haven't made one yet.

Ensure that you view the in-game tutorial for crucial information on how to play the game, as otherwise you may be confused on what to do!

Starting a game is easy! Just press the NEW GAME button or LOAD GAME if you want to load a previously saved game. Returning to the main menu or exiting the entire application will always save your progress, so don't worry about losing your save if you need to quit!

You also are able to view a high score leaderboard of everyone who has played CARDBOUND on this system. Compete with each other for the best scores!

The Settings menu has a plethora of application settings and accessibility settings, ensuring most people will be able to play CARDBOUND with no roadblocks whatsoever!

When you want to leave, just hit the EXIT button! All your data will be saved as soon as you do!

<p align="right">(<a href="#cardbound">back to top</a>)</p>



<!-- ROADMAP -->
## User Guide

To navigate through CARDBOUND, you can use two different input methods. You can either navigate the game with a mouse, with a keyboard, or with both!

Mouse navigation is very straightforward, but keyboard controls have a bit more to them. The keyboard controls are as follows:
* UP/DOWN ARROW: Navigate up/down through menu buttons.
* SPACE: Confirm selection of menu button/activate attack button during gameplay.
* ESCAPE: Pause the game during gameplay.
* 1: Select card 1 of 4 during gameplay.
* 2: Select card 2 of 4 during gameplay.
* 3: Select card 3 of 4 during gameplay.
* 4: Select card 4 of 4 during gameplay.

### User and Game Save

All the user data and game save data are stored in jsons, and if for some reason you need to edit your user data or game data, you can find them structured like this:

**USER SAVE DATA**
```json
{
  "userID": 0,
  "username": "instructor",
  "password": "WorldsBestInstructor",
  "lifetimeGames": 0,
  "bestScore": 0,
  "bestTime": 0,
  "mostLevelsCleared": 0,
  "totalPlaytime": 42,
  "bestTimeString": "00:00:00",
  "totalPlaytimeString": "00:00:42",
  "masterVolume": 75,
  "musicVolume": 75,
  "sfxVolume": 75,
  "disableLevelTimer": false,
  "disableRunTimer": false,
  "unlHealth": false,
  "currentUser": false
}
```

**GAME SAVE DATA**
```json
{
    "userId": 1,
    "numLives": 3,
    "level": 1,
    "score": 0,
    "runTimer": "00:00:02",
    "enemyHp": 4,
    "cardsList": [
      [
        "Fish Flapper",
        "Slap the enemy with a mud-skipper dealing a devastating ? damage.",
        "attack",
        "fish_flapper.png",
        "2",
        "1"
      ],
      [
        "Fish Flapper",
        "Slap the enemy with a mud-skipper dealing a devastating ? damage.",
        "attack",
        "fish_flapper.png",
        "2",
        "1"
      ],
      [
        "Buff Up",
        "All your attacks deal an extra 3 damage.",
        "modifier",
        "buff_up.png",
        "x+3",
        "-1"
      ],
      [
        "Heaven\u0027s Nectar",
        "Ah yes! Rejuvenation! Buff next attack by an extra 50%",
        "modifier",
        "heavens_nectar.png",
        "1.5*x",
        "1"
      ]
    ]
  }
```

<p align="right">(<a href="#cardbound">back to top</a>)</p>



<!-- INSTRUCTOR MODE -->
## Instructor Mode

To access instructor mode, you must log in with the instructor username and password. The username and password are:
* Username: **instructor**
* Password: **WorldsBestInstructor**
<p align="right">(<a href="#cardbound">back to top</a>)</p>

<!-- DEBUG MODE -->
## Debug Mode

There is a debug mode you can access if you so please! There isn't anything too useful in there for anyone that isn't a developer, but it can be accessed by inputting the Sonic 3 cheat code on the main menu within 2 seconds, just like the good ol' days:

**UP UP DOWN DOWN UP UP UP UP**

Unfortunately, unlike Sonic 3 the debug mode here is not very fun, but if you need to test various flags and transitions then debug mode will be perfect!

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge
[forks-url]: https://github.com/othneildrew/Best-README-Template/network/members
[stars-shield]: https://img.shields.io/github/stars/othneildrew/Best-README-Template.svg?style=for-the-badge
[stars-url]: https://github.com/othneildrew/Best-README-Template/stargazers
[issues-shield]: https://img.shields.io/github/issues/othneildrew/Best-README-Template.svg?style=for-the-badge
[issues-url]: https://github.com/othneildrew/Best-README-Template/issues
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
[Javafx.com]: https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white
[Javafx-url]: https://openjfx.io/