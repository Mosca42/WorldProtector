WorldProtector 1.12
========

This is a reworked version of the WorldGuard plugin by Mosca42.

========

Introduction :

 

WorldProtector is a mod allows you to protect your constructions on your servers, it's based on the same principle as the plugin WorldGuard but with several differences at commands level.

Additions :

Thanks to the “regions stick” items, you can select the size you want and save it to define protection flags to it.

 

Currently, there are three types of commands :

-/region : allows to define, edit, cancel a region.

-/expand : allows to modify the different select points with the stick (height).

-/flag : allows to define one or several flags to a region to protect it (the region).

 

Currently, there are twenty flags :

Break : if active, it forbids players to break the region's blocks.

Place : if active, it forbids players to put on.

Explosions : if active, all explosions will be block in the region (doesn't work under Sponge).

Mob-spawning-monsters : if active, it forbids monsters to spawn in the region.

Mob-spawning-animal : if active, it forbids animals to spawn in the region.

Mob-spawning-all : if active, it forbids mobs to spawn in the region.

Use : if active, it forbids all player's interactions on all containers in the region.

Chest-acces : if active, it forbids all player's interactions on chest's containers in the region.

Invincible : if active, all players become invicible in the region.

Damage-players : if active, players can't be strike down in the region.

Enderpearls : if active, it forbids teleportation in the region thanks to a enderpearls.

Item-drop : if active, it forbids drop items

Exp-drop : if active, it forbids drop the experience

Creeper-explosions : if active, the Creeper explosions will be block in the region (doen't work under Sponge).

Other-explosions : if active, explosions outside Creeper will be block in the region (doesn't work under Sponge).

Damage-animals : if active, animals can't be hit in the region.

Damage-monsters : if active, monsters can't be hit in the region.

Send-chat : if active, it forbids players in the region to send messages in the chat (doesn't block the commands).

Fall-damage : if active, players don't have fall damage in the region.

Pickup-item : if active, players can't pick up items on the ground in the region.

If you have any ideas about flags, don't hesitate to propose it to me.
 
Create a region :

To create your region, follow these instructions :

-Select two points thanks to regions sticks,

-Think to expand your region if you want the region protects from the layer 0 to 255,

-/region define “your region's name”

-/flag add “your region's name” “flag's name”

If a region is inside another region you have to use the command : .region setpriority “your region's name” “priority (1,2,3,4,5,6...)”

Let's suppose that there is a arena player vs player in your spawn, it's necessary the arena gains the upper hand on the spawn to activate the flag “player vs player”, if not it will be deactivate.

So, you have to put the arena to a priority higher than the spawn to have the “player vs player” activate :

/region setpriority spawn 1

/region setpriority arena 2
 
1 is superior to 2 so the arena gains the upper hand on the spawn region.

Information :

The mod has to be install on the client and on the server too.
 
Special thanks :

Thanks to BrokenSwing for his help about regions and safeguard in the world.

Thanks to TheBossMax2 for the mod logo.



## License
This mod is open sourced under the Creative Commons 3.0 Attribution Non-Commercial License
https://creativecommons.org/licenses/by-nc/3.0/legalcode

Summary
https://creativecommons.org/licenses/by-nc/3.0/

You are free to:
* Share — copy and redistribute the material in any medium or format
* Adapt — remix, transform, and build upon the material
* The licensor cannot revoke these freedoms as long as you follow the license terms.

Under the following conditions:
* Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
* NonCommercial — You may not use the material for commercial purposes.
* No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
