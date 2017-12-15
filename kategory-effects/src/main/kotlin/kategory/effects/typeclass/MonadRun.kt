package kategory.effects

import kategory.MonadError
import kategory.Typeclass

interface MonadRun<F, E> : MonadError<F, E>, RunAsync<F, E>, RunSync<F, E>, Typeclass