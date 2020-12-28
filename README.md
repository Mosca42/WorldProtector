
# Introduction

WorldProtector is a mod allows you to protect your constructions on your servers, it's based on the same principle as the plugin WorldGuard but with several differences at commands level.

# Features

Thanks to the “regions stick” items, you can select the size you want and save it to define protection flags to it.

## Commands

Currently, there are three types of commands :

- /region : allows to define, edit, cancel a region.

- /expand : allows to modify the different select points with the stick (height).

- /flag : allows to define one or several flags to a region to protect it (the region).

## Flags
Currently, there are twenty flags :

- break : if active, it forbids players to break the region's blocks.

- place : if active, it forbids players to put on.

- explosions : if active, all explosions will be block in the region (doesn't work under Sponge).

- mob-spawning-monsters : if active, it forbids monsters to spawn in the region.

- mob-spawning-animal : if active, it forbids animals to spawn in the region.

- mob-spawning-all : if active, it forbids mobs to spawn in the region.

- use : if active, it forbids all player's interactions on all containers in the region.

- chest-acces : if active, it forbids all player's interactions on chest's containers in the region.

- invincible : if active, all players become invicible in the region.

- damage-players : if active, players can't be strike down in the region.

- enderpearls : if active, it forbids teleportation in the region thanks to a enderpearls.

- item-drop : if active, it forbids drop items

- exp-drop : if active, it forbids drop the experience

- creeper-explosions : if active, the Creeper explosions will be block in the region (doen't work under Sponge).

- other-explosions : if active, explosions outside Creeper will be block in the region (doesn't work under Sponge).

- damage-animals : if active, animals can't be hit in the region.

- damage-monsters : if active, monsters can't be hit in the region.

- send-chat : if active, it forbids players in the region to send messages in the chat (doesn't block the commands).

- fall-damage : if active, players don't have fall damage in the region.

- pickup-item : if active, players can't pick up items on the ground in the region.

If you have any ideas about flags, don't hesitate to propose it to me.



## Create a region

To create your region, follow these instructions :

1. Select two points thanks to regions sticks,

2. Think to expand your region if you want the region protects from the layer 0 to 255,

3. /region define “your region's name”

4. /flag add “your region's name” “flag's name”

## Region priority

If a region is inside another region you have to use the command : .region setpriority “your region's name” “priority (1,2,3,4,5,6...)”

Let's suppose that there is a arena player vs player in your spawn, it's necessary the arena gains the upper hand on the spawn to activate the flag “player vs player”, if not it will be deactivate.

So, you have to put the arena to a priority higher than the spawn to have the “player vs player” activate :

/region setpriority spawn 1

/region setpriority arena 2

1 is superior to 2 so the arena gains the upper hand on the spawn region.


# Additional information

- The mod has to be install on the client and on the server too
- You can use in custom modpack


# Special thanks


Thanks to BrokenSwing for his help about regions and safeguard in the world.

Thanks to TheBossMax2 for the mod logo.

DISCORD: [WorldProtector Discord](https://discord.gg/MsA8XPc)

# License
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
