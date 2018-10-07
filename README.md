NameSayer
=========

SOFTENG 206 Assignment 4

Target User
-----------

Our product is targeted at students who wish to learn their classmates' names.

Special features
----------------
* You are able to add names to the database through the record menu
* We have tooltips for the icons in the practice menu.
* You can stop recording by clicking the record button again.

Building and Use
----------------

* An executable jar has been included at the project root. Please execute it by typing `java -jar NameSayer.jar` into
  a terminal.
* For the names database to work, all recordings must be unzipped directly to the `names/` folder (for example, 
    `names/se206_2-5-2018_15-23-50_Mason.wav`)
* Bad quality ratings are stored in `bad_quality.txt` in the current working directory
* Attempts are stored in `names/attempts/<name>/`

* There are 3 menus to our application
    * Practice, in which you can select recordings to practice using the check boxes by each name. Clicking 'start'
      will take you to a practice interface where you can create practice attempts.
    * Listen, which allows you to view/remove database entries, and you can view your previous attempts by clicking on 
        the ellipsis next to each name
    * Record, which allows you to create new entries for the database itself (not attempts).


     _______________
    ( wow namesayer )
     ---------------
       o
        o
            .--.
           |o_o |
           |:_/ |
          //   \ \
         (|     | )
        /'\_   _/`\
        \___)=(___/
    
