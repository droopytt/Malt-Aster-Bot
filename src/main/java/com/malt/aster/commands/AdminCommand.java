package com.malt.aster.commands;

/**
 * Tagging interface for administrator commands.
 *
 * Used by the command manager to check if a command is for administrators only.
 * Should be implemented by any Command that should only be used by administrators (by definition, they have the
 * administrator permission in the server the command is called in)
 */
public interface AdminCommand {}
