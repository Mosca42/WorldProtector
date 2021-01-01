
# Introduction

WorldProtector is a mod allows you to protect your constructions on your servers, 
it's based on the same principle as the plugin WorldGuard but with several differences at command level.

# Features

The Region Stick allows you to mark an area (cuboid) for which you can then specify a name.
For this region it is possible to apply flags that prevent certain actions/events to protect it.

## Commands

Currently, there are three types of commands :

- /wp region: allows to define, edit, remove a region.

- /wp expand: allows to modify the Y level (height) of the marked region.

- /wp flag: allows to define one or several flags (rules) for a region to protect it.

## Flags
Currently, there are twenty one flags :

- break: if active, it forbids players to break the region's blocks.

- place: if active, it forbids players to put on.

- explosions: if active, all explosions will be blocked in the region (doesn't work under Sponge).

- mob-spawning-monsters: if active, it forbids monsters to spawn in the region.

- mob-spawning-animal: if active, it forbids animals to spawn in the region.

- mob-spawning-all: if active, it forbids mobs to spawn in the region.

- use: if active, it forbids all player's interactions on all containers in the region.

- chest-access: if active, it forbids all player's interactions on chest containers in the region.

- invincible: if active, all players become invincible in the region.

- damage-players: if active, players can't be strike down in the region.

- enderpearls: if active, it forbids teleportation in the region thanks to a enderpearls.

- item-drop: if active, it forbids to drop items

- exp-drop: if active, it forbids to drop experience

- creeper-explosions: if active, the Creeper explosions will be blocked in the region (doesn't work under Sponge).

- other-explosions: if active, explosions outside Creeper will be blocked in the region (doesn't work under Sponge).

- damage-animals: if active, animals can't be hit in the region.

- damage-villagers: if active, villagers can't be hit in the region.

- damage-monsters: if active, monsters can't be hit in the region.

- send-chat: if active, it forbids players in the region to send messages in the chat (doesn't block the commands).

- fall-damage: if active, players don't have fall damage in the region.

- pickup-item: if active, players can't pick up items on the ground in the region.

If you have any ideas about flags, don't hesitate to propose it to me.


## Create a region

To create your region, follow these instructions :

1. Use the region stick to mark the two blocks which define the area of the region

2. [Optional] Expand the Y level (height) of your marked region with the ```/wp expand vert [<Y1>] [<Y2>]``` command (default is 0 to 255).

3. Define your region: ```/wp region define <your region's name>```

4. Add flags to your region: ```/wp flag add <your region's name> <flag name>```


## Region priority

If a region is inside another region you have to use the command 
```/wp region setpriority <your region's name> <priority (1,2,3,4,5,6...)>``` to give a specific region more priority over another.

Let's suppose that there is a pvp arena in your spawn. Your spawn forbids pvp, but your in your arena you want to have pvp allowed.
Then it's necessary that the arena gains the upper hand over the spawn to activate, if not pvp will not be possible.

Therefore, the arena region needs to have a higher priority than the spawn region. The following example show how to achieve this:

```/wp region setpriority spawn 1```

```/wp region setpriority arena 2```

2 is superior to 1, so that the arena gains the upper hand over the spawn region and PvP is possible.

# Additional information

- The mod has to be installed on the client and on the server too
- You can use this mod in a custom modpack


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
