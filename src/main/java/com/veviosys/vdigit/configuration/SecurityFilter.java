package com.veviosys.vdigit.configuration;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.google.gson.Gson;
import com.veviosys.vdigit.license.DcryptClass;
import com.veviosys.vdigit.models.TempSession;

import com.veviosys.vdigit.repositories.TempSessionRepo;
import com.veviosys.vdigit.services.CostumUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private MySessionRegistry sessionRegistry;
    @Autowired
    Lic lic;
    @Autowired
    DcryptClass dcryptClass;
    @Autowired
    private TempSessionRepo sessionRepo;

    @Transactional
    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            FilterChain filterChain) throws ServletException, IOException {
        try {

            List<TempSession> sessions = null;
            try {
                sessions = sessionRepo
                        .findByUserId(((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal())
                                        .getUser().getUserId());
            } catch (Exception e) {
            }
            if (Objects.nonNull(sessions)) {
                String cookie = httpRequest.getHeader("cookie").replace("JSESSIONID=", "");
                for (TempSession session : sessions) {
                    if (cookie.equals(session.getSession())) {
                        sessionRepo.delete(session);
                        httpResponse.addHeader("Access-Control-Expose-Headers", "Autho");
                        httpResponse.setHeader("Autho", "non");
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        List<String> urlsToVerify = new ArrayList<String>();
        // urlsToVerify.add("/documaniacourrier/api/v1/uslog");
        urlsToVerify.add("/api/v1/uslog");

        // Boolean veBsss = true;
        boolean canLoggin = true;

        if (urlsToVerify.contains(httpRequest.getRequestURI())) {
            // lic.vef();
            // veBsss = lic.lastCheck();
            canLoggin = dcryptClass.readfile();
        }

        if (!canLoggin) {

            Map<String, String> err = new HashMap<String, String>();
            err.put("KOLIC", "KOLIC");
            httpResponse.addHeader("content-type", "application/json");
            httpResponse.getWriter().write((new Gson()).toJson(err));

        } else
            filterChain.doFilter(httpRequest, httpResponse);
    }
}
