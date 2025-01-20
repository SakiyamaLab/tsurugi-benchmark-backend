package com.tsurugidb;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.tsurugidb.iceaxe.TsurugiConnector;
import com.tsurugidb.iceaxe.transaction.manager.TgTmSetting;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;
import com.tsurugidb.iceaxe.session.TgSessionOption;
import com.tsurugidb.iceaxe.session.TgSessionOption.TgTimeoutKey;
import com.tsurugidb.iceaxe.session.TsurugiSession;
import com.tsurugidb.tsubakuro.channel.common.connection.UsernamePasswordCredential;


public class Main {
    public static void main(String[] args) {
        var endpoint = URI.create("tcp://localhost:12345");
        var credential = new UsernamePasswordCredential("user", "password");
        var connector = TsurugiConnector.of(endpoint, credential);
        var sessionOption = TgSessionOption.of().setTimeout(TgTimeoutKey.DEFAULT, 1, TimeUnit.MINUTES);
        try {
            var session = connector.createSession(sessionOption);
            var sql     = "select * from customer";
            try (var ps = session.createStatement(sql)) {
                var setting = TgTmSetting.ofAlways(TgTxOption.ofOCC());
                var tm = session.createTransactionManager(setting);
                tm.execute(transaction -> {
                    transaction.executeAndGetCount(ps);
                });
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}