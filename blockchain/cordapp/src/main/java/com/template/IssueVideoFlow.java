package com.template;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;
import java.util.stream.Collectors;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class IssueVideoFlow extends FlowLogic<Void> {
    private final String identifier;
    private final double similarityScore;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public IssueVideoFlow(String identifier, double similarityScore) {
        this.identifier = identifier;
        this.similarityScore = similarityScore;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        final List<AbstractParty> everyone = getServiceHub().getNetworkMapCache().getAllNodes().stream()
                .map(it -> it.getLegalIdentities().get(0))
                .collect(Collectors.toList());

        final VideoState video = new VideoState(identifier, similarityScore, getOurIdentity(), getOurIdentity(), everyone);
        final VideoContract.Commands.Issue issueCommand = new VideoContract.Commands.Issue();

        final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(video, VideoContract.ID)
                .addCommand(issueCommand, getOurIdentity().getOwningKey());

        final SignedTransaction stx = getServiceHub().signInitialTransaction(txBuilder);

        subFlow(new FinalityFlow(stx));

        return null;
    }
}
