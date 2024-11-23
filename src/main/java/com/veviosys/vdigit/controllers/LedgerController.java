package com.veviosys.vdigit.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import com.veviosys.vdigit.services.LedgerService;
import com.veviosys.vdigit.services.mailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("api/v1/ledger")
public class LedgerController {
    @Autowired
    LedgerService ledgerService;
    @Autowired
    mailService mail;

    @RequestMapping(path = "save/{id}/{save}", method = RequestMethod.POST)
    public void saveInLedger(@PathVariable UUID id, @PathVariable int save,
            @RequestBody(required = false) List<String> receivers, @RequestHeader(name = "secondary") String secondary)
            throws IOException, AddressException, MessagingException {
        if (Objects.nonNull(receivers)) {

            this.mail.sendCourrierDocs(id, receivers);
        }
        if (save == 1) {
            ledgerService.setupAndSave(id, secondary);
        }
            

    }
}
