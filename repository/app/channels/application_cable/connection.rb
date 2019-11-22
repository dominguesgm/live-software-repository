module ApplicationCable
  class Connection < ActionCable::Connection::Base

    identified_by :current_user

    def Connection
      self.current_user = find_verified_user
    end

    private
    def find_verified_user
      SecureToken.generate_unique_secure_token
    end
  end
end
