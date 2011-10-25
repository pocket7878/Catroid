package at.tugraz.ist.catroid.test.tutorial;

import android.content.Context;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.tutorial.State;
import at.tugraz.ist.catroid.tutorial.StateAppear;
import at.tugraz.ist.catroid.tutorial.StateController;
import at.tugraz.ist.catroid.tutorial.StateDisappear;
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
	public void testAppearState() {
		Tutor tutorDog = new Tutor(context.getResources(), context, Tutor.TutorType.DOG_TUTOR);
		Tutor tutorCat = new Tutor(context.getResources(), context, Tutor.TutorType.CAT_TUTOR);
		StateController stateControllerCat = new StateController(context.getResources(), tutorCat);
		StateController stateControllerDog = new StateController(context.getResources(), tutorDog);
		stateControllerCat.changeState(StateAppear.enter(stateControllerCat, context.getResources(),
				Tutor.TutorType.CAT_TUTOR));
		stateControllerDog.changeState(StateDisappear.enter(stateControllerDog, context.getResources(),
				Tutor.TutorType.DOG_TUTOR));
		stateControllerCat.updateAnimation(Tutor.TutorType.CAT_TUTOR);
		State currentState = stateControllerCat.getState();
		String nameOfCurrentState = currentState.getStateName();
		assertEquals("StateController not in StateAppear after one updateAnimation", "StateAppear", nameOfCurrentState);
		for (int i = 0; i < 10; i++) {
			stateControllerCat.updateAnimation(Tutor.TutorType.CAT_TUTOR);
			stateControllerDog.updateAnimation(Tutor.TutorType.DOG_TUTOR);
		}
		stateControllerDog.changeState(StateDisappear.enter(stateControllerDog, context.getResources(),
				Tutor.TutorType.DOG_TUTOR));
		currentState = stateControllerCat.getState();
		nameOfCurrentState = currentState.getStateName();
		assertEquals("StateController did not switch back to StateIdle as expected", "StateIdle", nameOfCurrentState);
	}

	/*
	 * Idle should stay Idle, independent of other tutors.
	 * Also tutors should start in idle, if not explicitely forced to a state.
	 */
	public void testIdleState() {
		//		Tutor tutorDog = new Tutor(context.getResources(), context, Tutor.TutorType.DOG_TUTOR);
		//		Tutor tutorCat = new Tutor(context.getResources(), context, Tutor.TutorType.CAT_TUTOR);
		//		StateController stateControllerCat = new StateController(context.getResources(), tutorCat);
		//		StateController stateControllerDog = new StateController(context.getResources(), tutorDog);
	}

	/*
	 * If the tutor is pushed to say something, the state changes to talking. It should go back to idle
	 * after some time(depending on length of text).
	 */
	public void testTalkState() {
		//		Tutor tutorCat = new Tutor(context.getResources(), context, Tutor.TutorType.CAT_TUTOR);
		//		StateController stateControllerCat = new StateController(context.getResources(), tutorCat);
		//		tutorCat.say("This is fancy pancy test text for testing the Statepattern of our little catroid tutors");

	}

	public void testDisappearState() {
	}

	public void testPointState() {
	}

	public void testJumpState() {
	}

}