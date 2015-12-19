This plugin was based off of McftProfiler http://dev.bukkit.org/bukkit-plugins/mcftprofiler/

This plugin gives staff members the ability to keep accounts on players, place sticky notes on them, see their previous usernames, and much more!

Requirements:
- MySQL database

Features
- Add notes to a players
- View online and offline player locations
- Track players' IPs and be notified of their possible alts! (This is all IP based, similar names will never be taken into account)
- Contact Mojang's API to look up the previous usernames of the players!
- Display a list of assocaited player accounts when a player joins, and if they are banned or not! (Tries to find CommandBook, if not found, uses Bukkit's ban system. Post a feature request if you'd like another ban plugin to be included!)

Optional dependencies
- VanishNoPacket http://dev.bukkit.org/bukkit-plugins/vanish/
     - Users who are vanished will have their "online time" displayed to people who cannot see them as the time they vanished.
- CommandBook http://dev.bukkit.org/bukkit-plugins/commandbook/
     - Hooks into CommandBook to display alts if they are banned according to it.

Commands:
    /status <playername | uuid> - Short for /MCProfiler info
    /MCProfiler addnote <playerName> <note>  - Adds a note to the given player
    /MCProfiler readnotes <playerName>  - Displays the notes on the given player
    /MCProfiler info <playerName|uuid>  - Displays a summary of the player
    /MCProfiler lookup <ip>  - Displays all accounts linked to the given IP
    /MCProfiler listlinks -r <playerName>  - Displays all accounts that might be linked to the given user. Use the -r flag for recursive player searching. This displays all of the alts of the player's alts, and all the alts of those alts....
    /MCProfiler listips <playerName>  - Lists all known IPs from a given player
    /MCProfiler uuid <uuid>  - Displays a username based on a UUID.
    /MCProfiler maintenance <fixnotes | forcemakeaccount> <args>  - Performs maintence.
    /MCProfiler maintenance fixnotes <UUID> <name>   - Associates a playername with the UUID.
    /MCProfiler maintenance forcemakeaccount UUID lastKnownName IP  - Forces an account to be made for /MCProfiler info. If you don't know the IP, type in NULL.
    /MCProfiler reload - Reloads general configuration settings.
Permissions:
    mcprofiler.help:
      description: Allows usage of /MCProfiler help
    mcprofiler.addnote:
      description: Allows usage of /MCProfiler addnote
    mcprofiler.notifiedofalts:
      description: Allows player to recieve notification of players alts.
    mcprofiler.info.basic:
      description: Allows user to get a name on /MCProfiler info
    mcprofiler.info.basic.uuid:
      description: Allows user to get UUID on /MCProfiler info
    mcprofiler.info.basic.previoususernames:
      description: Allows user to get previous usernames on /MCProfiler info
    mcprofiler.info.online:
      description: Allows user to get Online or last online status on /MCProfiler info
    mcprofiler.info.ip:
      description: Allows user to get IP on /MCProfiler info
    mcprofiler.info.position:
      description: Allows user to get last position on /MCProfiler info
    mcprofiler.uuid:
      description: Allows usage of /MCProfiler uuid
    mcprofiler.listlinks:
      description: Allows usage of /MCProfiler listlinks
    mcprofiler.lookup:
      description: Allows usage of /MCProfiler lookup
    mcprofiler.readnotes:
      description: Allows usage of /MCProfiler readnotes. Allows user to read notes on /MCProfiler info.
    mcprofiler.reload:
      description: Allows usage of /MCProfiler reload
    mcprofiler.listips:
      description: Allows usage of /MCProfiler listips

Config:

Installation:     