/*
 * Copyright (C) 2017, Ulrich Wolffgang <ulrich.wolffgang@proleap.io>
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package io.proleap.cobol.asg.metamodel.procedure.xml.impl;

import io.proleap.cobol.CobolParser.XmlGenerateStatementContext;
import io.proleap.cobol.asg.metamodel.ProgramUnit;
import io.proleap.cobol.asg.metamodel.Scope;
import io.proleap.cobol.asg.metamodel.call.Call;
import io.proleap.cobol.asg.metamodel.procedure.NotOnExceptionClause;
import io.proleap.cobol.asg.metamodel.procedure.OnExceptionClause;
import io.proleap.cobol.asg.metamodel.procedure.StatementType;
import io.proleap.cobol.asg.metamodel.procedure.StatementTypeEnum;
import io.proleap.cobol.asg.metamodel.procedure.impl.StatementImpl;
import io.proleap.cobol.asg.metamodel.procedure.xml.XmlGenerateStatement;

public class XmlGenerateStatementImpl extends StatementImpl implements XmlGenerateStatement {

	protected Call countCall;

	protected final XmlGenerateStatementContext ctx;

	protected Call fromCall;

	protected NotOnExceptionClause notOnExceptionClause;

	protected OnExceptionClause onExceptionClause;

	protected Call receiverCall;

	protected final StatementType statementType = StatementTypeEnum.XML_GENERATE;

	public XmlGenerateStatementImpl(final ProgramUnit programUnit, final Scope scope,
			final XmlGenerateStatementContext ctx) {
		super(programUnit, scope, ctx);

		this.ctx = ctx;
	}

	@Override
	public Call getCountCall() {
		return countCall;
	}

	@Override
	public Call getFromCall() {
		return fromCall;
	}

	@Override
	public NotOnExceptionClause getNotOnExceptionClause() {
		return notOnExceptionClause;
	}

	@Override
	public OnExceptionClause getOnExceptionClause() {
		return onExceptionClause;
	}

	@Override
	public Call getReceiverCall() {
		return receiverCall;
	}

	@Override
	public StatementType getStatementType() {
		return statementType;
	}

	@Override
	public void setCountCall(final Call countCall) {
		this.countCall = countCall;
	}

	@Override
	public void setFromCall(final Call fromCall) {
		this.fromCall = fromCall;
	}

	@Override
	public void setNotOnExceptionClause(final NotOnExceptionClause notOnExceptionClause) {
		this.notOnExceptionClause = notOnExceptionClause;
	}

	@Override
	public void setOnExceptionClause(final OnExceptionClause onExceptionClause) {
		this.onExceptionClause = onExceptionClause;
	}

	@Override
	public void setReceiverCall(final Call receiverCall) {
		this.receiverCall = receiverCall;
	}

}
