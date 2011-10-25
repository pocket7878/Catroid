package at.tugraz.ist.catroid.test.tutorial;

import android.content.Context;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.tutorial.State;
import at.tugraz.ist.catroid.tutorial.StateAppear;
import at.tugraz.ist.catroid.tutorial.StateController;
import at.tugraz.ist.catroid.tutorial.Tutor;

public class StateControllerTest extends AndroidTestCase {

	private Context context;

	@Override
	protected void setUp() throws Exception {
		context = getContext();
	}

	/*
	 * AppearState has to go back to IdleState after some Updates.
	 * On the other hand it should not go back to idle for one tutor if only
	 * the State of an other Tutor got animationUpdates.
	 */
	public void testAppearState() throws IllegalArgumentException, IllegalAccessException {
		Tutor tutor = new Tutor(context.getResources(), context, Tutor.TutorType.CAT_TUTOR);
		StateController stateController = new StateController(context.getResources(), tutor);
		stateController.changeState(StateAppear.enter(stateController, context.getResources(),
				Tutor.TutorType.CAT_TUTOR));
		stateController.updateAnimation(Tutor.TutorType.CAT_TUTOR);
		State currentState = stateController.getState();
		String nameOfCurrentState = currentState.getStateName();
		assertEquals("StateController not in StateAppear after one updateAnimation", "StateAppear", nameOfCurrentState);
		for (int i = 0; i < 10; i++) {
			stateController.updateAnimation(Tutor.TutorType.CAT_TUTOR);
		}
		currentState = stateController.getState();
		nameOfCurrentState = currentState.getStateName();
		assertEquals("StateController did not switch back to StateIdle as expected", "StateIdle", nameOfCurrentState);
	}
}