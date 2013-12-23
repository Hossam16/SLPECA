/*************************************************************************
 *                                                                       *
 *  EJBCA: The OpenSource Certificate Authority                          *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.ejbca.ui.cli.infrastructure.command;

import org.ejbca.ui.cli.infrastructure.command.CommandBase;

/**
 * @version $Id$
 *
 */
public abstract class EjbcaCommandBase extends CommandBase{

    public String getImplementationName() {
        return "EJBCA CLI";
    }
}
