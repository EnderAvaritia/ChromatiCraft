/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Interfaces;

import Reika.ChromatiCraft.Registry.CrystalElement;



public interface CrystalRepeater extends CrystalReceiver, CrystalTransmitter {

	public int getSignalDegradation(boolean point);

	public int getThoughputBonus(boolean point);

	public int getThoughputInsurance();

	public int getSignalDepth(CrystalElement e);

	public void setSignalDepth(CrystalElement e, int d);

	public boolean checkConnectivity();

	public void onTransfer(CrystalSource src, CrystalReceiver r, CrystalElement element, int amt);

}
