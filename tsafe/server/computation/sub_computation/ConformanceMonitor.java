/*
 TSAFE Prototype: A decision support tool for air traffic controllers
 Copyright (C) 2003  Gregory D. Dennis

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package tsafe.server.computation.sub_computation;

import tsafe.common_datastructures.FlightTrack;
import tsafe.server.calculation.Calculator;
import tsafe.server.computation.ComputationMediator;

/**
 * ConformanceMonitor.java Determines to what degree a flight is conforming to
 * its planned route
 */
public class ConformanceMonitor extends ComputationColleagues {

	/**
	 * Calculator for distances and lat/long to x,y conversion
	 */
	private Calculator calculator;

	/**
	 * Sole Constructor
	 */
	public ConformanceMonitor(ComputationMediator mediator,
			Calculator calculator) {

		super(mediator);
		this.calculator = calculator;
	}

	/**
	 * Blunder Detection Algorithm Return true if the ftObserved is too far from
	 * ftExpected
	 */
	public boolean isBlundering(FlightTrack ftObserved, FlightTrack ftExpected) {
		double residual = generateResidual(ftObserved, ftExpected);

		return residual >= this.mediator.getParameters().cmResidualThreshold;
	}

	/**
	 * Residual Generator
	 */
	private double generateResidual(FlightTrack ftObserved,
			FlightTrack ftExpected) {
		int numFactors = 0;
		double sum = 0;

		// Add the lateral factor if it is turned on
		if (this.mediator.getParameters().cmLateralWeightOn) {
			double lateralDev = calculator.distanceLL(ftObserved.getLatitude(),
					ftObserved.getLongitude(), ftExpected.getLatitude(),
					ftExpected.getLongitude(), this.mediator.getBounds());
			double lateralResidual = lateralDev
					/ this.mediator.getParameters().cmLateralThreshold;
			sum += lateralResidual;
			numFactors++;
		}

		// Add the vertical factor if it is turned on
		if (this.mediator.getParameters().cmVerticalWeightOn) {

			double verticalDev = Math.abs(ftObserved.getAltitude()
					- ftExpected.getAltitude());

			double verticalResidual = verticalDev
					/ this.mediator.getParameters().cmVerticalThreshold;
			sum += verticalResidual;
			numFactors++;
		}

		// Add the angular factor if it is on
		if (this.mediator.getParameters().cmAngularWeightOn) {
			double angularDev = Math.abs(ftObserved.getHeading()
					- ftExpected.getHeading());
			sum += angularDev
					/ this.mediator.getParameters().cmAngularThreshold;
			numFactors++;
		}

		// Add the speed factor if it is on
		if (this.mediator.getParameters().cmSpeedWeightOn) {
			double speedDev = Math.abs(ftObserved.getAltitude()
					- ftExpected.getAltitude());
			sum += speedDev / this.mediator.getParameters().cmSpeedThreshold;
			numFactors++;
		}

		// Normalize the summation
		// If zero factors are involved, define the residual to be zero
		return numFactors == 0 ? 0 : sum / (double) numFactors;
	}
}