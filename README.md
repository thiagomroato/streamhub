# StreamHub

App "hub" de multimídia para Android: uma tela única com Netflix, Prime Video,
Disney+, HBO Max, YouTube, Globoplay, Spotify e Paramount+. Ao tocar em um
serviço:

- Se o app estiver instalado → abre ele diretamente (login e reprodução
  continuam no app oficial de cada serviço).
- Se não estiver instalado → abre a página do app na Play Store; se a Play
  Store não existir no aparelho, abre o site oficial no navegador.

## Por que não é um WebView com Netflix "dentro"?

Netflix, Prime Video, Disney+ e HBO Max bloqueiam a reprodução em WebView por
causa de proteção de conteúdo (DRM/Widevine) e dos próprios termos de uso.
Nenhum app de terceiros consegue contornar isso de forma legítima — por isso
o StreamHub abre o app oficial de cada serviço em vez de tentar embutir o
player.

## Como gerar o APK

1. Instale o [Android Studio](https://developer.android.com/studio).
2. Abra esta pasta (`StreamHub`) como projeto existente.
3. Deixe o Gradle sincronizar (primeira vez baixa dependências — precisa de
   internet).
4. Menu **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

Para gerar um APK assinado (release, para distribuir fora da Play Store):
**Build → Generate Signed Bundle / APK**, escolha APK, crie ou use um
keystore, e siga o assistente.

## Como adicionar/remover serviços

Edite `app/src/main/java/com/thiago/streamhub/StreamingService.kt` e
adicione um item na lista `StreamingCatalog.services` com:

- `name`: nome exibido
- `packageName`: pacote do app no Android (ex: `com.netflix.mediaclient`)
- `playStoreId`: geralmente igual ao `packageName`
- `webFallbackUrl`: site oficial do serviço

## Requisitos

- Android 8.0 (API 26) ou superior.
- Não requer login nem armazena credenciais — cada serviço mantém sua
  própria sessão.

## Fazendo tudo pelo tablet (sem PC)

Já tem um arquivo de automação (`.github/workflows/build.yml`) que compila o
APK na nuvem. Você só precisa de navegador e uma conta grátis no GitHub.

1. **Crie uma conta** em github.com (pelo navegador do tablet), se ainda não tiver.
2. **Crie um repositório novo**: botão "+" → "New repository" → dê um nome
   (ex: `streamhub`) → Create repository. Deixe como "Public" ou "Private",
   tanto faz.
3. **Suba os arquivos**: na página do repositório vazio, clique em
   "uploading an existing file". No Chrome do tablet dá pra arrastar a pasta
   `StreamHub` inteira (extraia o zip antes, usando o gerenciador de
   arquivos do tablet) para a área de upload. Se o navegador não aceitar
   pasta inteira, suba arquivo por arquivo respeitando as subpastas.
4. **Aguarde a Action rodar sozinha**: assim que o upload for commitado,
   vá na aba **Actions** do repositório. Vai aparecer um build rodando
   (ícone amarelo → depois vira verde quando terminar, leva uns 2-4 min).
5. **Baixe o APK**: clique no build verde concluído → role até
   **Artifacts** → toque em `StreamHub-debug-apk` para baixar um `.zip`
   contendo o `app-debug.apk`.
6. **Instale no Android**: abra o `.zip` baixado, extraia o `.apk`, toque
   nele para instalar (talvez precise permitir "instalar de fontes
   desconhecidas" nas configurações — o Android pede isso automaticamente
   na primeira vez).

Se preferir não usar GitHub, alternativa é o app **AIDE** (Play Store) — um
editor/IDE Android que abre projetos Gradle e compila o APK direto no
aparelho, sem nuvem e sem PC.
