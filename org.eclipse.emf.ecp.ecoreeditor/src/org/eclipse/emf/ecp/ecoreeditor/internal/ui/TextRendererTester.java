package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

import org.eclipse.emf.ecp.view.model.common.SimpleControlRendererTester;

public class TextRendererTester extends SimpleControlRendererTester {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecp.view.model.common.SimpleControlRendererTester#isSingleValue()
	 */
	@Override
	protected boolean isSingleValue() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecp.view.model.common.SimpleControlRendererTester#getPriority()
	 */
	@Override
	protected int getPriority() {
		return 10;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecp.view.model.common.SimpleControlRendererTester#getSupportedClassType()
	 */
	@Override
	protected Class<?> getSupportedClassType() {
		return String.class;
	}
}
