package br.uff.graduatesapi.security

import br.uff.graduatesapi.model.PlatformUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(private val user: PlatformUser) : UserDetails {
  override fun getAuthorities() = listOf(GrantedAuthority { user.currentRoleEnum.toString() })

  override fun isEnabled() = true

  override fun getUsername() = user.id.toString()

  override fun isCredentialsNonExpired() = true

  override fun getPassword() = user.password

  override fun isAccountNonExpired() = true

  override fun isAccountNonLocked() = true

  fun getRoles() = user.roles

}