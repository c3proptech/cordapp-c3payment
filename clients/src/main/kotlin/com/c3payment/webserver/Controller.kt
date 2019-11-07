package com.c3payment.webserver

import com.c3payment.flows.TransactionsFlow.Initiator
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.utilities.getOrThrow
import net.corda.core.identity.CordaX500Name
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = "/templateendpoint", produces = arrayOf("text/plain"))
    private fun templateendpoint(): String {
        return "Define an endpoint here."
    }

    @PostMapping(value = [ "/transaction" ], produces = [ TEXT_PLAIN_VALUE ], headers = [ "Content-Type=application/x-www-form-urlencoded" ])
    fun transactionadd(request: HttpServletRequest): ResponseEntity<String> {
        val receive     = request.getParameter("receive")
        val currency    = request.getParameter("currency")
        val type        = request.getParameter("type")
        val amount      = request.getParameter("amount").toLong()
        val txid        = request.getParameter("txid")
        val createAt    = request.getParameter("create_at").toLong()

        if(receive == null){
            return ResponseEntity.badRequest().body("Query parameter receive must not be null.\n")
        }
        if (amount <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter amount must be non-negative.\n")
        }

        val partyX500Name = CordaX500Name.parse(receive)
        val receiveParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party named $receive cannot be found.\n")

        return try {
            val signedTx = proxy.startTrackedFlow(::Initiator, receiveParty, currency, type, amount, txid, createAt).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }
}