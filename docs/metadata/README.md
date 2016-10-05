# Metadata
## Privacy Policy
Available [here](https://sneakycoders.github.io/privacy-policy/)

## Languages Supported
| Language             | Language Code | Country Code | App | Google Play |
|:---------------------|:-------------:|:------------:|:---:|:-----------:|
| English              | en            | en-US        | ☑  | ☑
| Chinese (simplified) | zh            | zh-CN        | ☑  | ☑
| Spanish              | es            | es-ES        | ☑  | ☑
| French               | fr            | fr-FR        | ☑  | ☑
| German               | de            | de-DE        | ☐  | ☐
| Portuguese           | pt            | pt-BR        | ☐  | ☐
| Japanese             | ja            | ja-JP        | ☑  | ☑
| Korean               | ko            | ko-KR        | ☐  | ☐
| Russian              | ru            | ru-RU        | ☐  | ☐
| Arabic               | ar            | ar-MA        | ☐  | ☐

## Texts
| Name              | Category    | Location                                                 | Restrictions                                                                                     |
|:------------------|:------------|:---------------------------------------------------------|:-------------------------------------------------------------------------------------------------|
| Title             | Google Play | ```docs/metadata/title/<CountryCode>.txt```              | Less or equal than 20 characters
| Short Description | Google Play | ```docs/metadata/short-description/<CountryCode>.txt```  | Less or equal than 80 characters
| Long Description  | Google Play | ```docs/metadata/long-description/<CountryCode>.txt```   | Less or equal than 4000 characters
| App Strings       | App         | ```app/src/main/res/values-<LanguageCode>/strings.xml``` | Translated app needs to be manually tested, checking that there are no overlaps between elements
