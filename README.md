# Introduction

WorldProtector is a mod allows you to protect your constructions on your servers, 
it's based on the same principle as the plugin WorldGuard but with several differences at command level.

# Features

## Sticks!

There are three different sticks, which can help you define regions, add and remove protection flags to them, as well as players.
Except for the marking, which has to be done with the Region Marker, it is possible to use this mod only with the command line.

The most common task can also be done without commands!

### Region Marker

The Region Marker allows you to mark an area (cuboid) for which you can then specify a name. For a region it is possible
to apply flags that prevent certain actions/events from happening to protect it. This can be done with the Flag Stick in
conjunction with the Region Stick.

### Region Stick

The Region Stick is used to select the region you want to edit.
When selected a region it is possible to add players to the region by first choosing the desired mode (add or remove player), and then hitting the player (don't worry it does not hurt).
Added players are then not affected by the flags. 

### Flag Stick

The Flag Stick is used to select the flag you want to add/remove to/from a region. 
Choose the corresponding mode (add or remove flag) and hold the Region Stick with the selected region in your offhand. Then hold-right click.

Analogous you can also put a bunch of name tags, named with the corresponding flag names in an anvil, into a container and shift-right click on the container to add or remove all flags in the container at once!

## Commands

Currently, there are three types of commands :

- ```/wp region```: allows to define, edit, remove a region.

- ```/wp expand```: allows to modify the Y level (height) of the marked region.

- ```/wp flag```: allows to define one or several flags (rules) for a region to protect it.

- ```/wp player```: allows to add/remove players to/from regions

Type /wp help for more information.


## Create a region

To define a region, follow these instructions:

1. Use the Region Marker to mark the two blocks which define the area of the region.

2. [Optional] Expand the Y level (height) of your marked region with the ```/wp expand vert [<Y1>] [<Y2>]``` command (
   default is 0 to 255) or by using the secondary function of the Region Marker (see Region Marker tooltip).

3. [Optional] Set the teleportation target of the region with SHIFT right-click on a block. You can teleport to this
   region later by using ```/wp region tp <your region's name>```. When not set, a default teleportation position will
   be calculated.

4. Define your region: ```/wp region define <your region's name>``` or by renaming the Region Marker in an anvil.

5. Add flags to your region by using the appropriate command ```/wp flag add <your region's name> <flag name>``` or by
   using the Flag Stick in conjunction with the Region Stick (see Flag Stick and Region Stick tooltips).

6. [Optional] Add players to your region which are allowed to bypass the flags by using the
   command ````/wp player add <your region's name> <playername>```` or by using the Region Stick (see Region Stick
   tooltip for more details).


## Flags

Currently, there are **48 flags** available:

- break: prevents players from breaking blocks
- place: prevents players from placing blocks
- ignite-explosives: prevents explosives from blowing up
- explosions-blocks: prevents all explosions from destroying blocks (doesn't work under Sponge)
- explosions-entities: prevents all explosions from damaging entities (doesn't work under Sponge)
- creeper-explosions-blocks: prevents explosions caused by Creepers to destroy blocks (doesn't work under Sponge)
- creeper-explosions-entities: prevents explosions caused by Creepers to damage entities (doesn't work under Sponge)
- other-explosions-blocks: prevents all other explosions from destroying blocks (doesn't work under Sponge)
- other-explosions-entities: prevents all other explosions from damaging entities (doesn't work under Sponge)
- tools-secondary: prevents all type of secondary tool actions (strip wood, till farmland, create paths)
- strip-wood: prevents wood from being stripped
- till-farmland: prevents farmland from being tilled
- shovel-path: prevents creation of path blocks
- trample-farmland: prevents all farmland trampling
- trample-farmland-player: prevents players from trampling farmland
- trample-farmland-other: prevents non-player entities from trampling farmland
- lightning: prevents entities being hit by lightning (or at least get hurt/transformed)
- animal-taming: prevents players from taming animals
- animal-breeding: prevents players from breeding animals
- animal-mounting: prevents players from mounting animals
- spawning-all: prevents spawning of all entities
- spawning-monsters: prevents spawning of monsters
- spawning-animal: prevents spawning of animals
- spawning-irongolem: prevents spawning of iron golems
- spawning-xp: prevents spawning of xp orbs completely
- use: prevents players to interact with most blocks like buttons, fences, doors, etc.
- use-bonemeal: prevents players from using bone meal
- access-container: prevents players from accessing most containers
- access-enderchest: prevents players from accessing their ender chest
- enderpearl-from: prevents ender pearl teleportation out of a region
- enderpearl-to: prevents ender pearl teleportation to a region
- enderman-teleport-from: prevents enderman from teleporting out of a region
- enderman-teleport-to: prevents enderman from teleporting to a region
- shulker-teleport-from: prevents shulkers from teleporting out of a region
- shulker-teleport-to: prevents shulkers from teleporting to a region
- item-drop: prevents players from dropping items
- item-pickup: prevents players from picking up items
- xp-drop-all: prevents all entities from dropping xp orbs
- xp-drop-monster: prevents monsters from dropping xp orbs
- xp-drop-other: prevents non-hostile entities from dropping xp orbs
- level-freeze: prevents the player levels from increasing/decreasing (xp orbs will still be picked up)
- xp-freeze: prevents the player from gaining xp from xp orbs
- attack-players: prevents players from damaging other players (PvP)
- attack-animals: prevents players from damaging animals
- attack-villagers: prevents players from damaging villagers
- attack-monsters: prevents players from damaging monsters
- invincible: prevents players from taking damage
- fall-damage: prevents entities from taking fall damage
- fall-damage-players: prevents players from taking fall damage
- fall-damage-animals: prevents animals from taking fall damage
- fall-damage-villagers: prevents villagers from taking fall damage
- fall-damage-monsters: prevents monsters from taking fall damage
- send-chat: prevents players from sending chat messages (doesn't block commands)

You are also able to add or remove all flags by using the special flag 'all'.

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

- The mod has to be installed on the client and on the server.
- You can use this mod in a custom modpack.

# Contribution

Found a bug? Or do you have an idea for a new flag or just a general suggestions for the mod?

Don't hesitate to propose them to me. Or even better: Open a
new [issue](https://github.com/Mosca42/WorldProtector/issues)!

# Special thanks

Thanks to BrokenSwing for his help about regions and safeguard in the world.

Thanks to TheBossMax2 for the mod logo.

Thanks to z0rdak for porting the mod to version 1.16.5 and adding new features in the process.

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


# Possible future features/improvements

* Provide simple gui for choosing flag/region, since there could be many to cycle through
* Define any shape as a region
* Region highlighting
* Configuration support
* CLI: flags per dimension
* CLI: add command to list regions you are standing in
* CLI: make region info commands available for all players
* CLI: add and remove multiple flags at once
* CLI: add option to define a region without the region marker
* Add configuration option to disable specific event checks completely for optimization and default blacklist/whitelist
  setting for regions
* Blacklist/Whitelist option for regions