/*
 * Copyright (c) 2016, INTech.
 *
 * This file is part of INTech's HighLevel.
 *
 *  INTech's HighLevel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  INTech's HighLevel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with it.  If not, see <http://www.gnu.org/licenses/>.
 */

package enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Symétrisation des "TurningStrategy"
 * @author Discord
 *
 */
public class SymmetrizedTurningStrategy
{
    /** Map contenant une stratégie pour clé, et son symétrique pour valeur */
    Map<TurningStrategy, TurningStrategy> mCorrespondenceMap = new HashMap<TurningStrategy, TurningStrategy>();

    /**
     * construit la map de correspondances
     */
    public SymmetrizedTurningStrategy()
    {
        mCorrespondenceMap.put(TurningStrategy.FASTEST, TurningStrategy.FASTEST);
        mCorrespondenceMap.put(TurningStrategy.LEFT_ONLY, TurningStrategy.RIGHT_ONLY);
        mCorrespondenceMap.put(TurningStrategy.RIGHT_ONLY, TurningStrategy.LEFT_ONLY);
    }


    public TurningStrategy getSymmetrizedTurningStrategy(TurningStrategy order)
    {
        return mCorrespondenceMap.get(order);
    }
}
