package com.er7.financeai.infra.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired private JavaMailSender mailSender;

    @Value("${com.er7.financeai.base.frontend.url}")
    private String baseFrontendUrl;

    public void sendEmail(String remetente, List<String> destinatarios, String token) {
        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
            helper.setSubject("Convite FINANCE.AI");
            helper.setText(generateInvitationEmailTemplate(remetente, token), true);

            mailSender.send(mimeMessage);
            System.out.println("\n\nE-MAIL ENVIADO COM SUCESSO\n\n");
        } catch (MessagingException e) {
            throw new RuntimeException("Problemas com o envio de e-mail", e);
        }
    }


    public String generateInvitationEmailTemplate(String recipientName, String token) {
        // Usa blocos de texto para uma string HTML mais legível, evitando concatenação e escapes.
        var link = this.baseFrontendUrl.concat("invitation/%s/accept").formatted(token);
        return  """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Você foi convidado para um grupo no FINANCE.AI!</title>
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); }
                    .header { background-color: #18181b; padding: 40px; text-align: center; color: #ffffff; }
                    .header h1 { color: #bef264; margin: 0; font-size: 28px; font-weight: 700; }
                    .content { padding: 40px; text-align: center; color: #333333; }
                    .details { background-color: #f8f9fa; padding: 20px; border-radius: 10px; margin: 20px 0; }
                    .features { margin: 30px 0; text-align: center}
                    .feature-item { display: flex; align-items: center; margin-bottom: 15px; }
                    .feature-icon { margin-right: 10px; color: #18181b; }
                    .content p { font-size: 16px; line-height: 1.6; margin: 0 0 24px; }
                    .content .message { font-size: 18px; font-weight: 600; color: #2c3e50; }
                    .group-name { color: #18181b; font-weight: 700; }
                    .button { background-color: #4CAF50; color: white; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: 600; display: inline-block; margin-top: 16px; }
                    .footer { padding: 20px 40px; text-align: center; font-size: 12px; color: #888888; border-top: 1px solid #f0f0f0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1 style="color: #bef264; text-decoration: none">FINANCEAI</h1>
                    </div>
                    <div class="content">
                        <p class="message">
                            Olá, Você foi convidado por <span style="font-weight: bold; color: #18181b;">%s</span> para participar do grupo no FINANCEAI.
                        </p>
                        <p>Juntos, vocês poderão organizar e gerenciar suas finanças de forma inteligente e colaborativa.</p>
                        <p style="font-size: 14px; margin-bottom: 5px; margin-top: 46px;">
                            📊 Acompanhe despesas em tempo real
                        </p>
                        <p style="font-size: 14px; margin-bottom: 5px;">
                            🤝 Gerencie finanças em grupo
                        </p>
                        <p style="font-size: 14px; margin-bottom: 5px;">
                            📈 Visualize relatórios detalhados
                        </p>
                        <p style="font-size: 14px; margin-bottom: 5px;">
                            🎯 Defina e alcance metas financeiras
                        </p>
                        <a href="%s" class="button" style="background-color: #18181b; color: #bef264; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: 600; display: inline-block; margin-top: 46px;">Aceitar Convite</a>
                    </div>
                    <div class="footer">
                        <p>Este é um e-mail automático. Por favor, não responda.</p>
                        <p>&copy; 2025 FINANCEAI. Todos os direitos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(recipientName, link);
    }


}
