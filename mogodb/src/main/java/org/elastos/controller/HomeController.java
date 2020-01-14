/**
 * Copyright (c) 2020-2021 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.elastos.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class HomeController {
    @RequestMapping(value = "/",method = {RequestMethod.GET})
    public String welcome(){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "  <title>Welcome to did explorer api</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "  <h2>using explorer Api to access Elastos did data within a second</h2>\n" +
                "</body>\n" +
                "</html>";
    }
}
