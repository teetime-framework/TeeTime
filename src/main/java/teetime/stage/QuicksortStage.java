package teetime.stage;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractDCStage;
import teetime.framework.ConfigurationContext;

public final class QuicksortStage extends AbstractDCStage<List<Integer>> {

	public QuicksortStage(final ConfigurationContext context) {
		super(context);
	}

	@Override
	protected boolean splitCondition(final List<Integer> lis) {
		return (lis.size() >= 2) ? true : false;
	}

	@Override
	protected void divide(final List<Integer> element) {

		final int middle = (int) Math.ceil((double) element.size() / 2);
		final int pivot = element.get(middle);

		List<Integer> left = new ArrayList<Integer>();
		List<Integer> right = new ArrayList<Integer>();
		this.logger.debug("[DC]" + this.getId() + "_" + "dividing: " + element.toString());
		for (int i = 0; i < element.size(); i++) {
			if (element.get(i) <= pivot) {
				if (i == middle) {
					continue;
				}

				left.add(element.get(i));
			}
			else {
				right.add(element.get(i));
			}
		}
		left.add(pivot);
		this.logger.debug("[DC]" + this.getId() + "_" + "successfully divided... pivot:" + pivot);

		// recursively sort two sub parts
		leftOutputPort.send(left);
		rightOutputPort.send(right);
	}

	@Override
	protected void conquer(final List<Integer> eLeft, final List<Integer> eRight) {
		eLeft.addAll(eRight);
		outputPort.send(eLeft);
	}
}
