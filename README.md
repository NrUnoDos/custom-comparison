# custom-comparison

![Build](https://github.com/NrUnoDos/custom-comparison/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/26268-custom-comparison)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/26268-custom-comparison)

## Description

<!-- Plugin description -->
Extends the standard comparison in order to mark specific changes as not-relevant. 

Select "Custom Comparison" when comparing files / directories - it's in the same dropbox as "Do not ignore" and "Ignore whitespaces".

Setup patterns via the integrated settings dialog, that match the Psi elements you want to exclude from your diff view. 

Please note that the diff logic from this plugin takes place over the default diff logic, therefore 
overwriting language specific diff options like "Ignore imports".
<!-- Plugin description end -->

A more convenient way of setting up patterns is planned in the future.

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "custom-comparison"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/NrUnoDos/custom-comparison/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
