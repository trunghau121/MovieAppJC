# Screenshots:

<img src="https://github.com/trunghau121/MovieAppJC/assets/14820422/a5d2032c-8d7c-4317-863b-69361d83e862" width="300" height="630">
<img src="https://github.com/trunghau121/MovieAppJC/assets/14820422/5b50fb3a-0b14-42b5-8da7-81f1762700ba" width="300" height="630">
<img src="https://github.com/trunghau121/MovieAppJC/assets/14820422/f6f201f3-8a0b-4db5-81a7-8f33c00be5dc" width="300" height="630">
<img src="https://github.com/trunghau121/MovieAppJC/assets/14820422/03a9646a-a860-4533-9100-2ad6fb0c1998" width="300" height="630">
<img src="https://github.com/trunghau121/MovieAppJC/assets/14820422/a2f4b410-7d64-4185-81de-e55997923aa4" width="300" height="630">
<img src="https://github.com/trunghau121/MovieAppJC/assets/14820422/ade5e230-baf0-4540-b68f-7dfb0863c0e9" width="300" height="630">

## Built with using Jetpack Compose

| What            | How                        |
|----------------	|------------------------------	|
| 🎭 User Interface (Android)   | [Jetpack Compose](https://developer.android.com/jetpack/compose)                |
| 🏗 Architecture    | [Clean](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)                            |
| 💉 DI (Android)                | [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)                        |
| 🌊 Async            | [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)                |
| 🌐 Networking        | [Retrofit](https://square.github.io/retrofit/)                        |
| 📄 Parsing            | [Gson](https://github.com/google/gson)                            |

## Architecture

MovieAppJC follows the principles of Clean Architecture with Android Architecture Components.

### Architecture's layers & boundaries:

![image](https://github.com/trunghau121/MovieAppJC/assets/14820422/cf904f26-d5b4-4936-b423-f2c3e7381f3a)

**UI Layer**  contains  _UI (Activities, Composables, Fragments)_  that are coordinated by  _ViewModels which execute 1 or multiple UseCases._ Presentation Layer depends on Domain Layer.

**Domain Layer** is the most INNER part of the circle (no dependencies with other layers) and it
contains _Entities, Use cases & Repository Interfaces._ Use cases combine data from 1 or multiple
Repository Interfaces.

**Data Layer**  contains  _Repository Implementations and 1 or multiple Data Sources._  Repositories
are responsible to coordinate data from the different Data Sources. Data Layer depends on Domain
Layer.

### Conventions:

Files are suffixed with be defined Class types.

- ViewModels are suffixed with **ViewModel**. Ex: `HomeViewModel`
- UseCases are suffixed with **UseCase**. Ex: `DetailMovieUseCase`
- Models are suffixed with **Model**. Ex: `MovieModel`
- Entities are suffixed with **Entity**. Ex: `MovieEntity`
- DataSources are suffixed with **DataSource**. Ex: `AppLocalDataSource`, `MovieLocalDataSource`
- Repositories are suffixed with **Repository**. Ex: `MovieRepository`
