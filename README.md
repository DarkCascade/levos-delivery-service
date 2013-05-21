Levo's Delivery Service [Android Puzzle]
========================================

Description
-----------
As the new courier for Levo's Delivery Service, you're in charge of getting your customers' packages to their
destinations as fast as possible! Draw paths around town to find the most optimal route to your destination.
Watch out for traffic and other obstacles! Use cash from successful deliveries to buy better and faster vehicles!

Abstract
--------
Levo's Delivery Service is a top-down puzzler focused on allowing the player to discover the quickest ways around
town to deliver packages in the shortest amount of time. Offering quick gameplay and a sense of urgency through
diminishing time and mounting left turn totals (based on the player's play style), the game offers fast, exciting
gameplay with replay value through new vehicle purchases and new towns to explore.

Sample Gameplay
---------------
The player selects their town from the menu, and selects "Start Game". The player is presented with a top-down
view of the selected town, with available route nodes shown in gray. The game's timer will be reset to the town's
default value and shown to the player. The available package sources are highlighted
in green. The player will draw a route from the courier's current position to the package source of their choice,
then continue the route to that package's destination. The player can carry up to a certain number of packages at once
(determined by their vehicle). Available destinations based on currently held packages will be highlighted as well.
Levo (the owner of the delivery service) has a serious fear of left turns. Using GPS embedded in the player's
vehicle, he will dock pay for each left turn taken by the player. All left turns will result in a message being
displayed shortly (something like "Left Turn!" in red). The game will track all left turns made and will deduct
accordingly at the end of the session. When time runs out, the player will be shown their results: total packages
delivered, cash gained, and cash lost through left turns. The player is then taken to the menu, with the ability
to buy items and vehicles at the store, select a new town, or quit. The player's current cash total will be displayed
on the menu screen for easy reference.

Main Menu
---------
From here, the player can select a new town, visit the store, or begin a new round. Three buttons are presented:
  - Select Town:
      The player will be presented with the list of currently available towns. The interface should allow for more
      towns to be shown that are either locked or coming soon.
  - Store:
      The player will be presented with a list of available purchases using in-game cash. New vehicles are available,
      as well as one-off powerups allowing left turns, more time, or more cash for successful deliveries.
  - Start Game:
      The currently selected town will be initialized, and the player will be presented with the main play screen.

Town Selection Menu
-------------------
The currently available towns will be presented as buttons. Each button will show the town name, traffic density, 
size, and a background picture in the theme of the town. This menu should allow scrolling to see all towns. Locked
towns should show the same information as unlocked towns, but with a light gray overlay and a lock icon. Conditions
for unlocking towns have yet to be determined.

Store
-----
The store allows the player to spend their in-game cash on powerups and vehicles. The vehicles should have various
values like speed and carrying capacity to differentiate. The player should be able to quickly compare their current
vehicle's stats to a selected store vehicle. The player can only have one vehicle at a time.
One-time powerups are also available from the store. These will only affect the next play session. Example powerups
include:
  - Left Turns Allowed: the next play session will not deduct cash from the player's score for left turns.
  - Slow Time: the game timer will be slowed, allowing the player more time to deliver packages.
  - Double Cash: the next play session will award double cash for successful deliveries.
These powerups are relatively cheap to encourage the player to pick them up without worrying too much about breaking
the bank. The vehicles, on the other hand, are expensive, and will require a few to several play sessions to save
up enough cash.

Start Game
----------
The game will initialize the currently selected town and move the player to the main play screen.

Main Game Play
--------------
--------------
The primary play screen consists of an image of the currently selected town, and a grid of available routes. The grids
are light gray squares along roads. The image will show buildings and such to represent package sources. As new
packages become available, little package icons will pop up on the screen. When the player has a package on their
vehicle, destinations will show up on the map. The player can draw routes of any length. When the route passes over
an available package, the player will pick that package up if their vehicle has room. When the route passes over
an available destination, the player will gain time and cash for the delivery, and the package will be removed from
their vehicle, allowing them to pick up another one. The game will continue until time runs out. Any time a left turn
is executed, the player should be notified and the game should increment a counter.

When the round has ended, the total time, total cash, and total left turns should be shown to the player. The resulting
cash total minus number of left turns (multiplied by a penalty per left turn) will then result in a net cash gained.
This net total should be applied to the player's cash total, and the player will be returned to the main menu.

Traffic Density
---------------
Each town's traffic density affects the speed of traffic lights, as well as the number of other cars on the road.
These obstacles will inhibit the player's progress.

Town Size
---------
The bigger a town is, the more package sources and destinations it has, and the more roads and available routes it has.
Bigger towns will allow the player more freedom, but also increase the traffic density to compensate. Bigger towns
should offer the player more incentive in the form of time and cash for successful deliveries.

