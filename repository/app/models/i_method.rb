class IMethod < ApplicationRecord
    belongs_to :i_class

    has_many :arguments, dependent: :destroy
    has_many :method_invocations, dependent: :destroy
end
