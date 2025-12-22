package com.terracafe.terracafe_backend.controller.view;

import com.terracafe.terracafe_backend.model.Transaction;
import com.terracafe.terracafe_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionViewController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public String listTransactions(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        model.addAttribute("title", "Transactions");
        return "transactions/list";
    }
}
