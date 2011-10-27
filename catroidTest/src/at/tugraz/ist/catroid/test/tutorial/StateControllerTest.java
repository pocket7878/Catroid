package at.tugraz.ist.catroid.test.tutorial;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.tutorial.State;
import at.tugraz.ist.catroid.tutorial.StateAppear;
import at.tugraz.ist.catroid.tutorial.StateController;
import at.tugraz.ist.catroid.tutorial.StateDisappear;
import at.tugraz.ist.catroid.tutorial.StatePoint;
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
		Tutor tutorDog = new Tutor(context.getResources(), context, Tutor.TutorType.DOG_TUTOR);
		Tutor tutorCat = new Tutor(context.getResources(), context, Tutor.TutorType.CAT_TUTOR);
		StateController stateControllerCat = new StateController(context.getResources(), tutorCat);
		StateController stateControllerDog = new StateController(context.getResources(), tutorDog);
		State currentStateCat = stateControllerCat.getState();
		State currentStateDog = stateControllerDog.getState();
		String nameOfCurrentStateCat = currentStateCat.getStateName();
		String nameOfCurrentStateDog = currentStateDog.getStateName();
		assertEquals("StateController should be in IdleState after Init", "StateIdle", nameOfCurrentStateCat);
		assertEquals("StateController should be in IdleState after Init", "StateIdle", nameOfCurrentStateDog);
		for (int i = 0; i < 10; i++) {
			stateControllerCat.updateAnimation(Tutor.TutorType.CAT_TUTOR);
			stateControllerDog.updateAnimation(Tutor.TutorType.DOG_TUTOR);
		}
		assertEquals("StateController should be in IdleState after Init and with no Statechange", "StateIdle",
				nameOfCurrentStateCat);
		assertEquals("StateController should be in IdleState after Init and with no Statechange", "StateIdle",
				nameOfCurrentStateDog);
		stateControllerDog.changeState(StateDisappear.enter(stateControllerDog, context.getResources(),
				Tutor.TutorType.DOG_TUTOR));
		nameOfCurrentStateCat = stateControllerCat.getState().getStateName();
		assertEquals("StateController for Cat should stay in State Idle, even if other tutors state changes",
				"StateIdle", nameOfCurrentStateCat);
	}

	/*
	 * If the tutor is pushed to say something, the state changes to talking. It should go back to idle
	 * after some time(depending on length of text).
	 */
	public void testTalkState() {
		Tutor tutorCat = new Tutor(context.getResources(), context, Tutor.TutorType.CAT_TUTOR);
		StateController stateControllerCat = new StateController(context.getResources(), tutorCat);
		tutorCat.say("This is fancy pancy test text for testing the Statepattern of our little catroid tutors");
		assertEquals("StateController should go to State Talk, if Tutor has something to say", "StateTalk",
				stateControllerCat.getState().getStateName());

		// sollte durchrennen in dem State bis von der Bubble befehl kommt in Idle zurueckzugehen
		// evtl. Test mit Zeit? macht das Sinn in dem Fall? Oder ist das der Test fuer die Bubble?
	}

	public void testDisappearState() {
		Tutor tutorDog = new Tutor(context.getResources(), context, Tutor.TutorType.DOG_TUTOR);
		Tutor tutorCat = new Tutor(context.getResources(), context, Tutor.TutorType.CAT_TUTOR);
		StateController stateControllerCat = new StateController(context.getResources(), tutorCat);
		StateController stateControllerDog = new StateController(context.getResources(), tutorDog);
		stateControllerCat.changeState(StateDisappear.enter(stateControllerCat, context.getResources(),
				Tutor.TutorType.CAT_TUTOR));
		stateControllerDog.changeState(StateAppear.enter(stateControllerDog, context.getResources(),
				Tutor.TutorType.DOG_TUTOR));
		stateControllerCat.updateAnimation(Tutor.TutorType.CAT_TUTOR);
		State currentState = stateControllerCat.getState();
		String nameOfCurrentState = currentState.getStateName();
		assertEquals("StateController not in StateDisappear after one updateAnimation", "StateDisappear",
				nameOfCurrentState);
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

	public void testPointState() {
		// Kleiner Versuch
		long maxHeap = Runtime.getRuntime().maxMemory();

		//

		Tutor tutorDog = new Tutor(context.getResources(), context, Tutor.TutorType.DOG_TUTOR);
		StateController stateControllerDog = new StateController(context.getResources(), tutorDog);
		stateControllerDog.changeState(StatePoint.enter(stateControllerDog, context.getResources(),
				Tutor.TutorType.DOG_TUTOR));
		stateControllerDog.updateAnimation(Tutor.TutorType.DOG_TUTOR);
		Bitmap previousBitmap = stateControllerDog.updateAnimation(Tutor.TutorType.DOG_TUTOR);
		Bitmap currentBitmap;
		for (int i = 0; i < 5; i++) {
			currentBitmap = stateControllerDog.updateAnimation(Tutor.TutorType.DOG_TUTOR);
			assertFalse("Same Bitmap after updateAnimation, should be a different one",
					areBitmapsTheSame(previousBitmap, currentBitmap));
			previousBitmap = currentBitmap;
		}
	}

	public boolean areBitmapsTheSame(Bitmap bitmap1, Bitmap bitmap2) {
		ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
		bitmap1.copyPixelsToBuffer(buffer1);

		ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
		bitmap2.copyPixelsToBuffer(buffer2);

		return Arrays.equals(buffer1.array(), buffer2.array());
	}

	public void testJumpState() {

	}

}