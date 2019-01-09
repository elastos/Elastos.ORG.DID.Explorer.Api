package org.elastos.service;

import org.elastos.POJO.InputDidStatus;
import org.elastos.entity.ReturnMsgEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElaDidChainServiceTest {
    @Autowired
    ElaDidChainService elaDidChainService;

    @Test
    public void getDIDProperties() throws Exception {
        ReturnMsgEntity ret;
        ret = elaDidChainService.getDIDProperties("ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", InputDidStatus.all, null, null);
        System.out.println("all:"+ret.getResult());
        ret = elaDidChainService.getDIDProperties("ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", InputDidStatus.normal, null, null);
        System.out.println("normal:"+ret.getResult());
        ret = elaDidChainService.getDIDProperties("ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", InputDidStatus.deprecated, null, null);
        System.out.println("deprecated:"+ret.getResult());

        ret = elaDidChainService.getDIDProperties("ijZ71xbJ7tduDybqRSENcJdJt", InputDidStatus.all, null, null);
        System.out.println("deprecated:"+ret.getResult());
        ret = elaDidChainService.getDIDProperties("test_del_did", InputDidStatus.all, null, null);
        System.out.println("deprecated:"+ret.getResult());
    }

    @Test
    public void getDIDPropertyValue() throws Exception {
        ReturnMsgEntity ret;
        ret = elaDidChainService.getDIDPropertyValue("ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", "del_in_end");
        System.out.println("property:"+ret.getResult());
        ret = elaDidChainService.getDIDPropertyValue("ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", "del_in_middle");
        System.out.println("property:"+ret.getResult());

        ret = elaDidChainService.getDIDPropertyValue("test_del_did", "test_del_did");
        System.out.println("deprecated:"+ret.getResult());
    }

    @Test
    public void getDIDPropertyHistory() throws Exception {
        ReturnMsgEntity ret;
        ret = elaDidChainService.getDIDPropertyHistory("ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", "del_in_end", null, null);
        System.out.println("history:"+ret.getResult());
        ret = elaDidChainService.getDIDPropertyValue("test_del_did", "test_del_did");
        System.out.println("deprecated:"+ret.getResult());
        ret = elaDidChainService.getDIDPropertyHistory("ijZ71xbJ7tduDybqRSEHgjaRtKzBNcJdJt", "publicKey", 3, 2);
        System.out.println("history:"+ret.getResult());
    }

}