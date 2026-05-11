package simulation;

import OSPAnimator.AnimItem;
import OSPAnimator.AnimQueue;

import java.awt.Graphics2D;

public class AnimQueueItem extends AnimItem {
	private final AnimQueue queue;

	public AnimQueueItem(AnimQueue queue) {
		super();
		this.queue = queue;
		setAlwaysVisible(true);
	}

	@Override
	public void drawItem(Graphics2D g2d, double time) {
		if (queue != null) {
			queue.draw(g2d);
		}
	}
}
