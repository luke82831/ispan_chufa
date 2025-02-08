package com.ispan.chufa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ispan.chufa.util.AccessToken;
import com.ispan.chufa.util.LineUserInfo;
import com.ispan.chufa.util.Verify;

@Service
public class LineAPIService {
    @Value("${line.login.channel-id}")
    private String channelId;

    @Value("${line.login.channel-secret}")
    private String channelSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public AccessToken issueToken(String code, String redirectUri) {
        // 1. 準備 headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 2. 準備 body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", channelId);
        body.add("client_secret", channelSecret);

        // 3. 呼叫 LINE Token API
        String tokenUrl = "https://api.line.me/oauth2/v2.1/token";
        ResponseEntity<AccessToken> response = restTemplate.postForEntity(tokenUrl, new HttpEntity<>(body, headers),
                AccessToken.class);

        // 4. 回傳結果
        return response.getBody(); // 可加防錯處理
    }

    public AccessToken refreshToken(AccessToken oldToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", oldToken.getRefresh_token());
        body.add("client_id", channelId);
        body.add("client_secret", channelSecret);

        String tokenUrl = "https://api.line.me/oauth2/v2.1/token";
        ResponseEntity<AccessToken> response = restTemplate.postForEntity(tokenUrl, new HttpEntity<>(body, headers),
                AccessToken.class);

        return response.getBody();
    }

    public Verify verify(AccessToken token) {
        if (token == null || token.getAccess_token() == null)
            return null;

        String verifyUrl = "https://api.line.me/oauth2/v2.1/verify?access_token=" + token.getAccess_token();
        ResponseEntity<Verify> response = restTemplate.getForEntity(verifyUrl, Verify.class);

        return response.getBody();
    }

    public void revoke(AccessToken token) {
        if (token == null || token.getAccess_token() == null)
            return;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("access_token", token.getAccess_token());
        body.add("client_id", channelId);
        body.add("client_secret", channelSecret);

        String revokeUrl = "https://api.line.me/oauth2/v2.1/revoke";
        restTemplate.postForEntity(revokeUrl, new HttpEntity<>(body, headers), String.class);
    }

    /**
     * 使用 AccessToken 獲取用戶信息
     * 
     * @param accessToken AccessToken
     * @return LineUserInfo 包含 displayName 和 email
     */
    public LineUserInfo getUserInfo(AccessToken accessToken) {
        String url = "https://api.line.me/v2/profile";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getAccess_token());
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<LineUserInfo> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, LineUserInfo.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch user info from LINE API", e);
        }
    }

}
