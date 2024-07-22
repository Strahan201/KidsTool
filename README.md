# KidsTool

This is a plugin that is meant to make it easier to deal with kids playing.  There are actually a few parts, not just the plugin.  I also use an AutoHotKey macro and a key interdiction program.  AutoHotKey allows for remapping the F keys to perform special functions and KeyHostages it the interdiction program that will prevent then from inadvertently hitting the Windows key or something and causing them to be thrown out of the game.

This Minecraft plugin is the tool I use to give them special F key functions.  I have mine setup so they can hit F1 to toggle day or night, F2 to toggle creative mode, F3 to heal themselves, F4 to give them a super bow kit, F5 to spawn an Enderdragon, etc.

# AutoHotKey

The macro file I made can be [downloaded here](https://www.sylvcraft.com/MinecraftMacros.ahk).  Edit it with a text editor and just configure it for whatever commands you want to be used.  It's designed for AutoHotKey 1 (I made this a long time ago lol) but it may work with newer versions.  Dunno, I haven't had any reason to upgrade yet.  You can get [AutoHotKey 1 here](https://www.autohotkey.com/download/ahk-install.exe).

# KeyHostages

This program will capture and block certain keys to prevent inadvertent keystrokes from screwing things up.  You can [download it here](https://www.sylvcraft.com/keyhostages.zip).  I won't waste my time putting a bunch of documentation on it here, you can check the KeyHostages Github page for that.  The zip I'm linking to here is preconfigured for Minecraft.

# KidsTool Plugin

This is the main engine that will run on your server to handle the custom functionality.  I haven't made an in game configuration tool for it, and honestly I likely won't.  It seems like the most time I spend on plugin dev is UI stuff, lol.  I really liked how LuckPerms has a web interface for configuring it; I think I'll try my hand at that.  But for now, just edit the yml file to set it up.  Here is some information on things you'll want to know about the plugin.

## Commands

All commands are under the /kt master command.  Here are the kt subcommands:

- reload
  - *Reloads configuration*
- item
  - *Spawns an item*

## Permissions

- kidstool.item.(item id)
  - *Allows them to get the specified item code with the /kt item command*
- kidstool.kit.(kit id)
  - *Allows them to get the specified kit with the /kt kit command*
- kidstool.power.(power id)
  - *Determines if they have the right to use powers.  Can be used to restrict certain powers from an existing item, so like if you have a sword with high power, healing and lightning strike you can take the lightning power away from a particular user who spams the crap out of it and annoys you without taking it away from everyone using that sword lol*
- kidstool.reload
  - *Reloads configuration from disk*

## Configuration

Here is an example config, showing the various features available:

```
items:
  (item id):
    lore:
    - lore string
    name: 'display name'
    powers:
      click_left:
        spawnmob:
          CREEPER:
            qty: 1
            charged: false
            health: 50
            held: BOW
        lightning:
          location: reticule
        teleport:
          location: reticule
        command:
          command: command
          qty: 1
        break:
          dropitem: true
          cube: false
        tame:
        fireball:
        instakill:
        heal:
      click_right:
      pickup:
      drop:
kits:
  (kit name):
    0:
      item: (item id)
      qty: 1
    1:
      (item stack serialized)
```
