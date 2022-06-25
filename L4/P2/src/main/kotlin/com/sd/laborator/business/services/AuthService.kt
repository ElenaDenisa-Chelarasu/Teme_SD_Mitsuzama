package com.sd.laborator.business.services
import com.sd.laborator.business.helpers.MyPasswordEncoderFactory
import com.sd.laborator.business.interfaces.IAuthService
import com.sd.laborator.business.models.MatchPasswordModel
import com.sd.laborator.business.models.MyCustomResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
@Service
class AuthService: IAuthService {
    @Autowired
    private lateinit var _delegatingPasswordEncoder: PasswordEncoder
    override fun encodePassword(password: String, encodingId: String):
            MyCustomResponse<Any>{
        try{
    // returneaza encoder-ul de parole conform algoritmului specificat
            val passwordEncoder =
                MyPasswordEncoderFactory.getEncoder(encodingId)
            if (passwordEncoder != null){
                return MyCustomResponse(
                    successfulOperation = true,
                    code=200,
                    data=passwordEncoder.encode(password)
                )
            }
//id invalid al encoder-ului specificat
            return MyCustomResponse(
                successfulOperation = false,
                code = 400,
                data = Unit,
                error = "Invalid encodingId=${encodingId}.",
                message = "Choose from:${MyPasswordEncoderFactory.getPasswordEncodings().joinToString(", ")}",)
        } catch (e: Exception){
            return MyCustomResponse(
                successfulOperation = false,
                code = 500,
                data = Unit,
                error = e.message
            )
        }
    }
    override fun matchPassword(matchModel: MatchPasswordModel):
            MyCustomResponse<Any> {
        return try{
// adaugam encodingId-ul in parola hash-uita pentru a fi delegat un PasswordEncoder corespunzator
            val currentHashPasswordWithAlg =
                "{${matchModel.encodingId}}${matchModel.hashPassword}"
            MyCustomResponse(successfulOperation = true,
                code=200,
                data=hashMapOf("areMatched" to _delegatingPasswordEncoder.matches(matchModel.password,currentHashPasswordWithAlg))
            )
        } catch (e: Exception){
            MyCustomResponse(
                successfulOperation = false,
                code = 500,
                data = Unit,
                error = e.message
            )
        }
    }
}