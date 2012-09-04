package tsafe.server.computation.sub_computation;

import tsafe.server.computation.ComputationMediator;

/**
 * @author Christopher Ackermann
 * 
 * Interface class for the colleague classes that participate on the mediator
 * pattern in the computation component. This is necessary in order to meet the
 * requirements of the mediator pattern.
 */
public abstract class ComputationColleagues {

	/**
	 * Handle to the mediator object.
	 */
	protected ComputationMediator mediator;

	/**
	 * Constructor sets the mediator attribute of this class.
	 * 
	 * @param server
	 *            The mediator object.
	 */
	public ComputationColleagues(ComputationMediator mediator) {
		this.mediator = mediator;
	}

}