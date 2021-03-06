package hu.elte.txtuml.examples.garage.control.model;

import hu.elte.txtuml.api.model.Action;
import hu.elte.txtuml.api.model.From;
import hu.elte.txtuml.api.model.ModelClass;
import hu.elte.txtuml.api.model.To;
import hu.elte.txtuml.api.model.Trigger;
import hu.elte.txtuml.api.stdlib.timers.Timer;
import hu.elte.txtuml.api.stdlib.world.World;
import hu.elte.txtuml.examples.garage.control.model.associations.KeyboardProvidesCode;
import hu.elte.txtuml.examples.garage.control.model.associations.KeyboardUsesTimer;
import hu.elte.txtuml.examples.garage.control.model.signals.external.in.KeyPress;
import hu.elte.txtuml.examples.garage.control.model.signals.external.out.Progress;
import hu.elte.txtuml.examples.garage.control.model.signals.internal.KeyboardTimeout;
import hu.elte.txtuml.examples.garage.control.model.signals.internal.KeyboardTimerExpired;
import hu.elte.txtuml.examples.garage.control.model.signals.internal.WaitForCode;

public class Keyboard extends ModelClass {
	int keyboardTimerCount;
	int keyboardTimerMaxCount = 100;

	public class InitKeyboard extends Initial {
	}

	@From(InitKeyboard.class)
	@To(Idle.class)
	public class TInitKeyboard extends Transition {
	}

	public class Idle extends State {
		@Override
		public void entry() {
			keyboardTimerCount = 0;
		}
	}

	public class Waiting extends State {
	}

	@From(Idle.class)
	@To(Idle.class)
	@Trigger(KeyPress.class)
	public class TSpontaneousKeyPress extends Transition {
		@Override
		public void effect() {
			Alarm a = Keyboard.this.assoc(KeyboardProvidesCode.Receiver.class).one();
			Action.send(getTrigger(KeyPress.class), a);
		}
	}

	@From(Idle.class)
	@To(Waiting.class)
	@Trigger(WaitForCode.class)
	public class TWaitForCode extends Transition {
		@Override
		public void effect() {
			keyboardTimerCount += 0;
			if (!assoc(KeyboardUsesTimer.timer.class).isEmpty()) {
				Timer timer = assoc(KeyboardUsesTimer.timer.class).one();
				Action.unlink(KeyboardUsesTimer.timer.class, timer, KeyboardUsesTimer.keyboard.class, Keyboard.this);				
			}
			Timer timer = Timer.schedule(new KeyboardTimerExpired(), Keyboard.this, 50);
			Action.link(KeyboardUsesTimer.timer.class, timer, KeyboardUsesTimer.keyboard.class, Keyboard.this);
		}
	}

	@From(Waiting.class)
	@To(Waiting.class)
	@Trigger(KeyboardTimerExpired.class)
	public class TRefreshProgress extends Transition {
		@Override
		public boolean guard() {
			return keyboardTimerCount < keyboardTimerMaxCount;
		}

		@Override
		public void effect() {
			keyboardTimerCount += 1;
			Action.send(new Progress(keyboardTimerCount), World.get(View.id()));
			assoc(KeyboardUsesTimer.timer.class).one().reset(50);
		}
	}

	@From(Waiting.class)
	@To(Idle.class)
	@Trigger(KeyPress.class)
	public class TExpectedKeyPress extends Transition {
		@Override
		public void effect() {
			Alarm a = Keyboard.this.assoc(KeyboardProvidesCode.Receiver.class).one();
			Action.send(getTrigger(KeyPress.class), a);
		}
	}

	@From(Waiting.class)
	@To(Idle.class)
	@Trigger(KeyboardTimerExpired.class)
	public class TTimeout extends Transition {
		@Override
		public boolean guard() {
			return keyboardTimerCount == keyboardTimerMaxCount;
		}

		@Override
		public void effect() {
			Alarm a = Keyboard.this.assoc(KeyboardProvidesCode.Receiver.class).one();
			Action.send(new KeyboardTimeout(), a);
		}
	}
}