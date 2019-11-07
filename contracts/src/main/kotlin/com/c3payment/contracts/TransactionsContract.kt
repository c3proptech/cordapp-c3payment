package com.c3payment.contracts

import com.c3payment.states.TransactionsState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction


class TransactionsContract : Contract {
    companion object {
        @JvmStatic
        val TRANSACTION_CONTRACT_ID = "com.c3payment.contracts.TransactionsContract"
    }

    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands.Create>()
        requireThat {
            // Generic constraints around the transaction.
            "No inputs should be consumed when issuing an Transactions." using (tx.inputs.isEmpty())
            "Only one output state should be created." using (tx.outputs.size == 1)
            val out = tx.outputsOfType<TransactionsState>().single()
            "The sender and the Receive cannot be the same entity." using (out.sender != out.receive)
            "All of the participants must be signers." using (command.signers.containsAll(out.participants.map { it.owningKey }))
            "The value must be non-negative." using (out.amount > 0)
        }
    }

    interface Commands : CommandData {
        class Create : Commands
    }
}
