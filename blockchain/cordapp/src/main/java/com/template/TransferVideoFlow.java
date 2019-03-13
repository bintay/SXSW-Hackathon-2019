//package com.template;
//
//import co.paralleluniverse.fibers.Suspendable;
//import net.corda.core.contracts.StateAndRef;
//import net.corda.core.flows.*;
//import net.corda.core.identity.Party;
//import net.corda.core.transactions.SignedTransaction;
//import net.corda.core.transactions.TransactionBuilder;
//import net.corda.core.utilities.ProgressTracker;
//
//// ******************
//// * Initiator flow *
//// ******************
//@InitiatingFlow
//@StartableByRPC
//public class TransferVideoFlow extends FlowLogic<Void> {
//    private final String identifier;
//    private final Party newOwner;
//    private final ProgressTracker progressTracker = new ProgressTracker();
//
//    public TransferVideoFlow(String identifier, Party newOwner) {
//        this.identifier = identifier;
//        this.newOwner = newOwner;
//    }
//
//    @Override
//    public ProgressTracker getProgressTracker() {
//        return progressTracker;
//    }
//
//    @Suspendable
//    @Override
//    public Void call() throws FlowException {
//        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
//
//        final StateAndRef<VideoState> VideoStateAndRef =
//
//        final VideoState newVideo = new VideoState(identifier, getOurIdentity(), getOurIdentity());
//        final VideoContract.Commands.Issue issueCommand = new VideoContract.Commands.Issue();
//
//        final TransactionBuilder txBuilder = new TransactionBuilder(notary)
//                .addOutputState(video, VideoContract.ID)
//                .addCommand(issueCommand, getOurIdentity().getOwningKey());
//
//        final SignedTransaction stx = getServiceHub().signInitialTransaction(txBuilder);
//
//        subFlow(new FinalityFlow(stx));
//
//        return null;
//    }
//}
