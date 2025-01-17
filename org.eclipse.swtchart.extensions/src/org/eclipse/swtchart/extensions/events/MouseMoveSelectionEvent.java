/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.swtchart.extensions.events;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.ISeriesSet;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IMouseSupport;

public class MouseMoveSelectionEvent extends AbstractHandledEventProcessor implements IHandledEventProcessor {

	@Override
	public int getEvent() {

		return IMouseSupport.EVENT_MOUSE_MOVE;
	}

	@Override
	public int getStateMask() {

		return SWT.BUTTON1;
	}

	@Override
	public void handleEvent(BaseChart baseChart, Event event) {

		/*
		 * Buffer the selection on the first event.
		 */
		IPlotArea plotArea = baseChart.getPlotArea();
		if(!plotArea.isBuffered()) {
			if(baseChart.getChartSettings().isBufferSelection()) {
				baseChart.suspendUpdate(true);
				Image image = new Image(Display.getDefault(), baseChart.getPlotArea().getImageData());
				ISeriesSet set = baseChart.getSeriesSet();
				ISeries<?>[] series = set.getSeries();
				for(ISeries<?> serie : series) {
					serie.setVisibleBuffered(serie.isVisible());
					baseChart.hideSeries(serie.getId());
				}
				/*
				 * The image will be disposed when releasing the selection
				 * and setting the background image to null.
				 */
				plotArea.setBackgroundImage(image);
				plotArea.getControl().setData(IPlotArea.KEY_BUFFERED_BACKGROUND_IMAGE, image);
				plotArea.setBuffered(true);
				baseChart.suspendUpdate(false);
				baseChart.redraw();
			}
		}
		/*
		 * Set Selection Range
		 */
		baseChart.getUserSelection().setStopCoordinate(event.x, event.y);
		baseChart.increaseRedrawCounter();
		if(baseChart.isRedraw()) {
			/*
			 * Rectangle is drawn here:
			 * void paintControl(PaintEvent e)
			 */
			baseChart.redraw();
			baseChart.resetRedrawCounter();
		}
	}
}
