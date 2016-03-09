package jd.plugins.hoster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jd.PluginWrapper;
import jd.http.Cookies;
import jd.nutils.encoding.Encoding;
import jd.parser.html.Form;
import jd.plugins.Account;
import jd.plugins.AccountInfo;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.components.UsenetConfigInterface;
import jd.plugins.components.UsenetServer;

import org.appwork.utils.StringUtils;

@HostPlugin(revision = "$Revision: 31032 $", interfaceVersion = 3, names = { "ssl-news.info" }, urls = { "" }, flags = { 0 })
public class SslNewsInfo extends UseNet {

    public SslNewsInfo(PluginWrapper wrapper) {
        super(wrapper);
        this.enablePremium("https://www.ssl-news.info/signup.php");
    }

    @Override
    public String getAGBLink() {
        return "https://www.ssl-news.info/conditions.en.pdf";
    }

    private final String USENET_USERNAME = "USENET_USERNAME";

    @Override
    protected String getUsername(Account account) {
        return account.getStringProperty(USENET_USERNAME, account.getUser());
    }

    public static interface SslNewsInfoConfigInterface extends UsenetConfigInterface {

    };

    @Override
    public Class<SslNewsInfoConfigInterface> getConfigInterface() {
        return SslNewsInfoConfigInterface.class;
    }

    @Override
    public AccountInfo fetchAccountInfo(Account account) throws Exception {
        setBrowserExclusive();
        final AccountInfo ai = new AccountInfo();
        br.setFollowRedirects(true);
        final Cookies cookies = account.loadCookies("");
        try {
            Form login = null;
            if (cookies != null) {
                br.setCookies(getHost(), cookies);
                br.getPage("https://www.ssl-news.info/myaccount.php?lang=en");
                login = br.getFormbyActionRegex("myaccount");
                if (login != null && login.containsHTML("username") && login.containsHTML("pword")) {
                    br.getCookies(getHost()).clear();
                } else if (br.getCookie(getHost(), "PHPSESSID") == null) {
                    br.getCookies(getHost()).clear();
                }
            }
            if (br.getCookie(getHost(), "PHPSESSID") == null) {
                account.clearCookies("");
                final String userName = account.getUser();
                br.getPage("https://www.ssl-news.info/myaccount.php");
                login = br.getFormbyActionRegex("myaccount");
                login.put("username", Encoding.urlEncode(userName));
                login.put("pword", Encoding.urlEncode(account.getPass()));
                br.submitForm(login);
                br.getPage("https://www.ssl-news.info/myaccount.php?lang=en");
                login = br.getFormbyActionRegex("myaccount");
                if (login != null && login.containsHTML("username") && login.containsHTML("pword")) {
                    throw new PluginException(LinkStatus.ERROR_PREMIUM, PluginException.VALUE_ID_PREMIUM_DISABLE);
                } else if (br.getCookie(getHost(), "PHPSESSID") == null) {
                    throw new PluginException(LinkStatus.ERROR_PREMIUM, PluginException.VALUE_ID_PREMIUM_DISABLE);
                }
            }
            account.saveCookies(br.getCookies(getHost()), "");
            final String userName = br.getRegex("\\[Username\\]\\s*=>\\s*(.*?)(\r|\n)").getMatch(0);
            if (userName == null) {
                throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
            } else {
                account.setProperty(USENET_USERNAME, userName.trim());
            }
            final String validUntil = br.getRegex("\\[aexpire\\]\\s*?=>\\s*?(\\d+)").getMatch(0);
            final String bucketType = br.getRegex("\\[Pakket\\]\\s*?=>\\s*(.*?)(\r|\n)").getMatch(0);
            if (bucketType != null) {
                ai.setStatus(Encoding.htmlOnlyDecode(bucketType));
                // https://www.ssl-news.info/signup.php
                if (StringUtils.containsIgnoreCase(bucketType, "block")) {
                    account.setMaxSimultanDownloads(30);
                } else if (StringUtils.containsIgnoreCase(bucketType, "shared")) {
                    account.setMaxSimultanDownloads(30);
                } else if (StringUtils.containsIgnoreCase(bucketType, "150") || StringUtils.containsIgnoreCase(bucketType, "250")) {
                    account.setMaxSimultanDownloads(30);
                } else if (StringUtils.containsIgnoreCase(bucketType, "flat 20") || StringUtils.containsIgnoreCase(bucketType, "flat 30")) {
                    account.setMaxSimultanDownloads(20);
                } else if (StringUtils.containsIgnoreCase(bucketType, "flat 10")) {
                    account.setMaxSimultanDownloads(10);
                } else if (StringUtils.containsIgnoreCase(bucketType, "flat 5")) {
                    account.setMaxSimultanDownloads(5);
                } else {
                    // smallest number of connections
                    account.setMaxSimultanDownloads(5);
                }
            } else {
                ai.setStatus("Unknown Type");
            }
            if (validUntil != null) {
                ai.setValidUntil(Long.parseLong(validUntil) * 1000);
            }
        } catch (final PluginException e) {
            if (e.getLinkStatus() == LinkStatus.ERROR_PREMIUM) {
                account.clearCookies("");
            }
            throw e;
        }
        ai.setProperty("multiHostSupport", Arrays.asList(new String[] { "usenet" }));
        return ai;
    }

    @Override
    public List<UsenetServer> getAvailableUsenetServer() {
        final List<UsenetServer> ret = new ArrayList<UsenetServer>();
        ret.addAll(UsenetServer.createServerList("reader.ssl-news.info", true, 443, 563, 600));
        return ret;
    }

}